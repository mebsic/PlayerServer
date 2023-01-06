echo "Starting network..."
sleep 3
cd ..
sudo /opt/lampp/lampp start
./start_bungee.sh
cd servers/lobby
./start_lobby.sh
cd ..
cd ..
echo "Done!"
