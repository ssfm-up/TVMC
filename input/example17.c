int turn = 0;
bool flag0 = false;
bool flag1 = false;
bool flag2 = false;
bool flag3 = false;
bool flag4 = false;

BEGIN_PROGRAM
int main() {
	while(true) {
		flag0 = true;
		turn = 1;
		while((flag1 || flag2 || flag3 || flag4) && !(turn == 0));
CRIT:	;
		flag0 = false;
	}
}
END_PROGRAM

BEGIN_PROGRAM
int main() {
	while(true) {
		flag1 = true;
		turn = 2;
		while((flag0 || flag2 || flag3 || flag4) && !(turn == 1));
CRIT:	;
		flag1 = false;
	}
}
END_PROGRAM

BEGIN_PROGRAM
int main() {
	while(true) {
		flag4 = true;
		turn = 0;
		while((flag0 || flag1 || flag2 || flag3) && !(turn == 4));
CRIT:	;
		flag4 = false;
	}
}
END_PROGRAM

BEGIN_PROGRAM
int main() {
	while(true) {
		flag2 = true;
		turn = 3;
		while((flag0 || flag1 || flag3 || flag4) && !(turn == 2));
CRIT:	;
		flag2 = false;
	}
}
END_PROGRAM

BEGIN_PROGRAM
int main() {
	while(true) {
		flag3 = true;
		turn = 4;
		while((flag0 || flag1 || flag2 || flag4) && !(turn == 3));
CRIT:	;
		flag3 = false;
	}
}
END_PROGRAM