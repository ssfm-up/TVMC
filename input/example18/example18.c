int x1 = 1;
int x2 = 1;
int x3 = 1;
int x4 = 1;

BEGIN_PROGRAM
int main() {
	while(x1 > 0) {
		x1 = x1 - 1;
	}
END:;
}
END_PROGRAM

BEGIN_PROGRAM
int main() {
	while(x2 > 0) { 
		x1 = x1 - 1;
		x2 = x2 - 1;
	}
END:;
}
END_PROGRAM

BEGIN_PROGRAM
int main() {
	while(x3 > 0) { 
		x2 = x2 - 1;
		x3 = x3 - 1;
	}
END:;
}
END_PROGRAM

BEGIN_PROGRAM
int main() {
	while(x4 > 0) { 
		x3 = x3 - 1;
		x4 = x4 - 1;
	}
END:;
}
END_PROGRAM