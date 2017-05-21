bool lock = false;
int x = 3;
int y = 1;
int i = 1;
int a [] = {1, 2, 4};

BEGIN_PROGRAM
/*Program [1]*/
int main() {
	a[i*2] = x;
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
	while(a[i] > 1) {
		i = 0;
		y = y - 1;
	}
END: ;
}
END_PROGRAM