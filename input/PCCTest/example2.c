int a = 0;
int b = 0;
int c = 0;
int d = 0;
int e = 0;
int f = 0;
int g = 0;

BEGIN_PROGRAM
void fun5() {
	for(f = 0; f < 6; ++f) {
		
	}
}

void fun4() {
	for(e = 0; e < 6; ++e) {
		fun5();
	}
}

void fun3() {
	for(d = 0; d < 6; ++d) {
		fun4();
	}
}

void fun2() {
	for(c = 0; c < 6; ++c) {
		fun3();
	}
}

void fun1() {
	for(b = 0; b < 6; ++b) {
		fun2();
	}
}

int main() {
	for(a = 0; a < 6; ++a) {
		fun1();
	}
	
	END: ;
}

END_PROGRAM