mutex fork1 = -1;
mutex fork2 = -1;
mutex fork3 = -1;
mutex fork4 = -1;
mutex fork5 = -1;

BEGIN_PROGRAM

int main() {
	while(true) {
		acquire fork1;
		acquire fork5;
EATING:	;
		release fork5;
		release fork1;
	}
}

END_PROGRAM

BEGIN_PROGRAM

int main() {
	while(true) {
		acquire fork2;
		acquire fork1;
EATING:	;
		release fork1;
		release fork2;
	}
}

END_PROGRAM

BEGIN_PROGRAM

int main() {
	while(true) {
		acquire fork3;
		acquire fork2;
EATING:	;
		release fork2;
		release fork3;
	}
}

END_PROGRAM

BEGIN_PROGRAM

int main() {
	while(true) {
		acquire fork4;
		acquire fork3;
EATING:	;
		release fork3;
		release fork4;
	}
}

END_PROGRAM

BEGIN_PROGRAM

int main() {
	while(true) {
		acquire fork5;
		acquire fork4;
EATING:	;
		release fork4;
		release fork5;
	}
}

END_PROGRAM