int i = 8;
int x = 12;
int y = 10;

BEGIN_PROGRAM

int main() {
	for(i = 0; i < 1; ++i) {		
		x = y;
	}
	END: ;
}

END_PROGRAM

BEGIN_PROGRAM
int main() {
	do {
		i--;
		if(x < y) 
			goto label2;
	}while(i > 0);
label2: ;		
}
END_PROGRAM