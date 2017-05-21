/*
Dijkstras Algorithmus fuer zwei Prozesse

*/

int turn = 1;

int flag1 = 0;
int flag2 = 0;

BEGIN_PROGRAM

int main() {
L1:	flag1 = 1; 
	while(!(turn == 1)) {
		if(turn == 2) {
			if(flag2 == 0) {
				turn = 1;
			}
		}
		/*else if(turn == 1) {
			if(flag1 == 0) {
				turn = 1;
			}
		}*/
	}
	flag1 = 2;
	
	if(flag2 == 2) {
		goto L1;
	}
	
CRIT1: ; /* Kritischer Bereich */

	flag1 = 0;	
}

END_PROGRAM

BEGIN_PROGRAM

int main() {
L2:	flag2 = 1; 
	while(!(turn == 2)) {
		/*if(turn == 2) {
			if(flag2 == 0) {
				turn = 2;
			}
		}
		else*/ if(turn == 1) {
			if(flag1 == 0) {
				turn = 2;
			}
		}
	}
	flag2 = 2;
	
	if(flag1 == 2) {
		goto L2;
	}
	
CRIT2: ; /* Kritischer Bereich */

	flag2 = 0;	
}

END_PROGRAM

