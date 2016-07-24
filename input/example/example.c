bool lock = false;
int x = 10;
int y = 1;
int i = 1;

BEGIN_PROGRAM
/*Program [1]*/
int main() {
	lock = true;
}
END_PROGRAM

BEGIN_PROGRAM
/*Program [2]*/
int main() {
	while(!lock) {
		y = y - 1;
		x = x - 1;
	}
Blubb: ;
}
END_PROGRAM

BEGIN_PROGRAM
/*Program [3]*/
int main() {
	while(y > 0) {
		y = y - 1;
	}
END: ;
}
END_PROGRAM