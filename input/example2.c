int x = 3;
int y = 3;

BEGIN_PROGRAM

int main() {
	while(x*y > x+y) {
		x--;
		y--;
	}
END: ;
}

END_PROGRAM
