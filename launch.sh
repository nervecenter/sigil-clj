#!/bin/sh
# launch.sh
# launches sigil server

export PATH="/usr/local/bin:/bin:/usr/bin:/usr/local/sbin:/usr/sbin:/sbin:/opt/aws/bin:/home/ec2-user/.local/bin:/home/ec2-user/bin"

cd /home/ec2-user/sigil-clj
sudo /usr/bin/java8 -jar /home/ec2-user/sigil-clj/target/sigil-clj-0.5.0-standalone.jar
