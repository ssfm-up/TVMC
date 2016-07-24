mutex m = -1;
int y = 0;

BEGIN_PROGRAM

int main() {
	while(true) {
		acquire m;
		y = 1; 
CS1:	release m;
	}
}

END_PROGRAM

BEGIN_PROGRAM

int main() {
	while(true) {
		acquire m;
	 	y = 2;
CS2:	release m;
	}
}

END_PROGRAM

BEGIN_PROGRAM

int main() {
	while(true) {
		acquire m;
	 	y = 3;
CS3:	release m;
	}
}

END_PROGRAM

BEGIN_PROGRAM

int main() {
	while(true) {
		acquire m;
	 	y = 4;
CS4:	release m;
	}
}

END_PROGRAM

BEGIN_PROGRAM

int main() {
	while(true) {
		acquire m;
	 	y = 5;
CS5:	release m;
	}
}

END_PROGRAM