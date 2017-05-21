int z = 1;

BEGIN_PROGRAM
int myfun(int a, int b) {
	if(a > b) {
		int tmp = a;
		a = b;
		b = tmp;		
	}
	return a+b;
}

int main() {
	z = myfun(-7,8);
	if(z > 0) {
END: ;
	}
}

END_PROGRAM