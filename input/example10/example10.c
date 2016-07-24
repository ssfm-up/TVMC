bool sperrflag = false;

BEGIN_PROGRAM

int main() {
	while(sperrflag) {
		; /* wait */
	}
	sperrflag = true;
CRIT1: ; /* kritischer Bereich */ 
	sperrflag = false;
}

END_PROGRAM

BEGIN_PROGRAM

int main() {
	while(sperrflag) {
		; /* wait */
	}
	sperrflag = true;
CRIT2: ; /* kritischer Bereich */ 
	sperrflag = false;
}

END_PROGRAM

