(ns sigil.actions.fuzzy
  (:require [clojure.string :as str]))


;; based off of https://blog.forrestthewoods.com/reverse-engineering-sublime-text-s-fuzzy-match-4cffeed33fdb

;; --- fuzzy match constants


(def adjacent-bonus 5)
(def seperator-bonus 10)
(def captial-match 15)
(def camel-bonus 10)
(def leading-letter-penalty -3)
(def max-leading-letter-penalty -9)
(def unmatched-letter-penalty -1)

(def not-nil? (complement nil?))

(defn fuzzy-match-finish
  [fuzzy-map]
  (let [final-score (+ (get fuzzy-map :score)
                       (if (get fuzzy-map :best-letter)
                         (get fuzzy-map :best-letter-score)
                         0))]
    [final-score]))

(defn next-or-rematch
  [{:keys [score term-idx item-idx prev-matched? prev-seperator? prev-lower? best-letter-score best-letter] :as partial-fuzzy} term-char item-char next-match?]
  (let [item-upper (str/upper-case item-char)
        item-lower (str/lower-case item-char)
        new-best-letter-score (+
                               (if prev-matched?
                                 adjacent-bonus
                                 0)
                               (if prev-seperator?
                                 seperator-bonus
                                 0)
                               (if (and prev-lower?
                                        (= item-char item-upper)
                                        (not= item-upper
                                              item-lower))
                                 camel-bonus
                                 0)
                               (if (= item-char item-upper)
                                 captial-match
                                 0))]
    (if (>= new-best-letter-score best-letter-score)
      (assoc partial-fuzzy :score (+ score
                                     (if (== 0 term-idx)
                                       (max (* item-idx leading-letter-penalty) max-leading-letter-penalty)
                                       0)
                                     (if (not-nil? best-letter)
                                       unmatched-letter-penalty
                                       0))
             :best-letter item-char
             :best-lower (str/lower-case item-char)
             :best-letter-score new-best-letter-score
             :prev-matched? true
             :term-idx (if next-match? (inc term-idx) term-idx)
             :prev-lower? (and (= item-char item-lower)
                                         (not= item-lower item-upper))
             :prev-seperator? (or (= item-char \_)
                                            (= item-char \ ))
             :item-idx (inc item-idx))
      (assoc partial-fuzzy :score (+ score
                                     (if (== 0 term-idx)
                                       (max (* item-idx leading-letter-penalty) max-leading-letter-penalty)
                                       0))
             :term-idx (if next-match? (inc term-idx) term-idx)
             :prev-matched? true
             :prev-lower? (and (= item-char item-lower)
                                         (not= item-lower item-upper))
             :prev-seperator? (or (= item-char \_)
                                            (= item-char \ ))
             :item-idx (inc item-idx)))))


(defn fuzzy-wuzzy
  [search-term item]
  (loop [fuzzy {:score 0
                :term-idx 0
                :item-idx 0
                :prev-matched? false
                :prev-lower? false
                :prev-seperator? true
                :best-letter nil
                :best-lower nil
                :best-letter-score 0}]
    (if (= (:item-idx fuzzy) (count item))
      (cons (= (:term-idx fuzzy) (count search-term)) (conj (fuzzy-match-finish fuzzy) item))
      (let [term-idx (:term-idx fuzzy)
            best-letter (:best-letter fuzzy)
            best-lower (:best-lower fuzzy)
            term-char (if (not= term-idx (count search-term))
                        (get search-term term-idx)
                        nil)
            item-char (get item (:item-idx fuzzy))
            term-lower (if (not-nil? term-char)
                         (str/lower-case term-char)
                         nil)
            item-lower (if (not-nil? item-char)
                         (str/lower-case item-char)
                         nil)
            item-upper (if (not-nil? item-char)
                         (str/upper-case item-char)
                         nil)
            next-match (and (= item-lower term-lower) term-char)
            re-match (and (= best-lower item-lower) best-letter)
            advanced (and next-match best-letter)
            term-repeat (and best-letter term-char (= best-lower term-lower))
            part-fuzzy (if (or advanced term-repeat)
                         (assoc fuzzy
                                :score (+ (:score fuzzy) (:best-letter-score fuzzy))
                                :best-letter nil
                                :best-lower nil
                                :best-letter-score 0)
                         fuzzy)]
        (recur (if (or next-match re-match)
                 (next-or-rematch part-fuzzy term-char item-char next-match)
                 (assoc part-fuzzy
                        :score (+ (:score part-fuzzy) unmatched-letter-penalty)
                        :prev-matched? false
                        :prev-lower? (and (= item-char item-lower)
                                          (not= item-lower item-upper))
                        :prev-seperator? (or (= item-char \_)
                                             (= item-char \ ))
                        :item-idx (inc (:item-idx part-fuzzy)))))))))


(defn test-fuzzy
  [search-str]
  (let [terms (mapv str/trim (str/split-lines (slurp "resources/NAMES.DIC")))]
    (take 5
          (reverse
           (sort-by #(second %) (filter #(first %)
                                      (pmap #(fuzzy-wuzzy search-str %) terms)))))))
