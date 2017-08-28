cd /home/roman/algoritmed2.com/algoritmed-mvp1-ehealth
gradle clean
rm -r bin/
cd ..
rm algoritmed-mvp1-ehealth.tar.gz
tar czf algoritmed-mvp1-ehealth.tar.gz algoritmed-mvp1-ehealth
scp algoritmed-mvp1-ehealth.tar.gz holweb@178.20.157.117:/home/holweb/server-medic
ssh -t holweb@178.20.157.117 "cd ~/server-medic/; bash; echo ./restart-am-medic.sh"
cd /home/roman/algoritmed2.com/algoritmed-mvp1-ehealth

