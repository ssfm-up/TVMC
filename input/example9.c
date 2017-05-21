mutex m = -1;
int y = 0;

BEGIN_PROGRAM

int main() {
	while(true) {
		acquire m;
		if(y == 0) {
CS1:		y = 1; 
		}
		release m;
	}
}

END_PROGRAM

BEGIN_PROGRAM

int main() {
	while(true) {
		acquire m;
		if(y == 0) {
CS2:		y = 2; 
		}
		release m;
	}
}

END_PROGRAM

BEGIN_PROGRAM

int main() {
	while(true) {
		acquire m;
		if(y == 0) {
CS3:		y = 3; 
		}
		release m;
	}
}

END_PROGRAM

BEGIN_PROGRAM

int main() {
	while(true) {
		acquire m;
		if(y == 0) {
CS4:		y = 4; 
		}
		release m;
	}
}

END_PROGRAM

BEGIN_PROGRAM

int main() {
	while(true) {
		acquire m;
		if(y == 0) {
CS5:		y = 5; 
		}
		release m;
	}
}

END_PROGRAM