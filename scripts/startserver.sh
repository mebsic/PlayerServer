JAR=paper.jar
MAXRAM=2048M
MINRAM=1024M

cd /network/BungeeCord/servers/$1
if [ -d "world" ]; then
	cd world
	rm -f -- session.lock
	cd ..
fi
if [ -d "world_nether" ]; then
	cd world_nether
	rm -f -- session.lock
	cd ..
fi
if [ -d "world_the_end" ]; then
	cd world_the_end
	rm -f -- session.lock
	cd ..
fi

screen -dmS $1 java -DIReallyKnowWhatIAmDoingISwear -Xmx$MAXRAM -Xms$MINRAM -jar $JAR --nogui
