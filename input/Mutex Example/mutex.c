mutex m = -1;
/*
EF((pc_0=1) /\ (pc_1=1))
=> should evaluate to 'F'
*/

BEGIN_PROGRAM

int main() {
	while(true) {
		UNCRITICAL: acquire m;
		CRITICAL : release m;	
	}
}

END_PROGRAM

BEGIN_PROGRAM

int main() {
	while(true) {
		UNCRITICAL: acquire m;
		CRITICAL : release m;	
	}
}

END_PROGRAM


