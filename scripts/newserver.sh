server=/network/BungeeCord/servers/$1

if [ -d "$server" ]; then
  ./deleteserver.sh $1
fi

cp -rf /network/BungeeCord/scripts/template /network/BungeeCord/servers/$1
