echo "Shutting down..."
sleep 3
screen -ls | grep '(Detached)' | awk '{print $1}' | xargs -I % -t screen -XS % quit
sleep 3
sudo /opt/lampp/lampp stop
echo "Done!"
