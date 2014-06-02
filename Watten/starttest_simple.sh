#!/bin/sh
BINDIR=~/eclipse-workspace/watten/Watten/bin/
CLIENT=com/mpp/testing/ClientTest
CLIENT2=com/mpp/network/ClientForConsole
SERVER=com/mpp/ui/Server

SERVERCMD="echo SERVER; cd $BINDIR; java.exe $SERVER; read -p 'Done. Press any key to close...' -n1 -s"
CLIENTCMD="echo CLIENT; cd $BINDIR; java.exe $CLIENT; read -p 'Done. Press any key to close...' -n1 -s"
CLIENT2CMD="echo CLIENT2; cd $BINDIR; java.exe $CLIENT2; /bin/bash"

tmux kill-session -t watten-test-session
tmux new -d -s watten-test-session "$SERVERCMD"\; \
          split-window -d "$CLIENT2CMD" \; \
		  selectp -t 1 \; \
                attach \;
				
echo Killing all internal java processes...
kill $(ps aux | grep 'java' | awk '{print $1}')
echo Done.
exit 0
