bool lock;
bool door1 = false;
bool door2 = false;
bool door3 = false;
bool door4 = false;
mutex m = -1;
/*
EF( door1 /\ door2 )
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

BEGIN_PROGRAM

int main() {
	while(true) {
		acquire m;
		door3 = true;
		door3 = false;
		release m;	
	}
}

END_PROGRAM

BEGIN_PROGRAM

int main() {
	while(true) {
		acquire m;
		door4 = true;
		door4 = false;
		release m;	
	}
}

END_PROGRAM





