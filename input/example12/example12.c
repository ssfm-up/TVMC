bool door1 = false;
bool door2 = false;
bool door3 = false;
bool door4 = false;
int x = 0;
int y = 0;
int z = 0;
int a = 0;
int b = 0;
int c = 0;


BEGIN_PROGRAM

int main() {
	while(true) {
		door1 = true;
		door1 = false;		
	}
}

END_PROGRAM

BEGIN_PROGRAM

int main() {
	while(true) {
		/*door2 = 1;*/
		door2 = false;
	}
}

END_PROGRAM

BEGIN_PROGRAM
int main() {
	x += y;
	b = y + a;
	while(a > b) {
		a *= x;
		z = x/y + a * b;
		if(z > 0)
			break;
	}
	x++;
	a = x++ * y++;
	z = 0;
	x += y;
	b = y + a;
	while(a > b) {
		a *= x;
		z = x/y + a * b;
		if(z > 0)
			break;
	}
	x++;
	a = x++ * y++;
	z = 0;
}

END_PROGRAM

BEGIN_PROGRAM


int main() {
	while(true) {
		door4 = true;
		door4 = false;		
	}
}
END_PROGRAM


