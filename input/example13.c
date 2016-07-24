int x = 1;
int y = 12;
int z = 123;
int m = 0;

BEGIN_PROGRAM

int main() {
  m = z;	
  if(y < z)
  {
	if(x < y)
	   m = y;
    else if(x < z)
	   m = x; 
  }
  else 
	if(x > y)
	  m = y;
  else if(x > z)
	  m = x;
END: ;
}

END_PROGRAM
	   
