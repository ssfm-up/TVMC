bool lock;
bool door1 = false;
bool door2 = false;
mutex m = -1;
/*
EF( (door1 = T) /\ (door2 = T) )
=> should evaluate to 'F'
*/

BEGIN_PROGRAM

int main() {
	while(true) {
		acquire m;
		door1 = true;
		door1 = false;
		release m;	
	}
}

END_PROGRAM

BEGIN_PROGRAM

int main() {
	while(true) {
		acquire m;
		door2 = true;
		door2 = false;
		release m;	
	}
}

END_PROGRAM


