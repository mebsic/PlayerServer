echo "Updating..."
echo "This may take a few seconds, please wait..."
sleep 3

./shutdown.sh
cd jars
wget http://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar
cp BungeeCord.jar /network/BungeeCord/BungeeCord.jar
rm -rf BungeeCord.jar.*
rm -rf BungeeCord.jar

for dir in /network/BungeeCord/servers/*/; do
    if [ -d "$dir" ]; then
	cp paper-*.jar "$dir"/paper.jar
    	cp PlayerServer.jar "$dir"/plugins/PlayerServer.jar
    fi
done

cp paper-*.jar /network/BungeeCord/scripts/template/paper.jar
cp PlayerServer.jar /network/BungeeCord/scripts/template/plugins/PlayerServer.jar
cp PlayerServer.jar /network/BungeeCord/plugins/PlayerServer.jar

cd ..
./start.sh
