#!/bin/ksh

echo $1 $2 $3
### $1 is the first argument, $2 is the second, etc.
# path of graph layout program
trap 'if [ ! -s dot.err ]; then /bin/rm -f dot.err; fi; exit' 0 1 2 3 15
DOT=dot
theGraph="$(/bin/cat)"
if [ -z "$theGraph" ]
then
    print -r - 'digraph newgraph { start [shape=ellipse, label="Start\nHere"]; }' | $DOT
else
    GRAPH=$(print -r - "$theGraph" | $DOT 2>dot.err)
    if [ $? -eq 0 ]
    then
	# need the pipe to get buffering
	# (otherwise output is truncated at 2048 bytes)
        print -r -  "$GRAPH" | /bin/cat
    else
        print -r - 'digraph error { n1 [shape=plaintext, label="Your graph had an error in it!"]; }' | $DOT
    fi
fi
print
if [ -s dot.err ]
then
	/bin/cat dot.err 1>&2
	/bin/rm -f dot.err
fi
exit 0
