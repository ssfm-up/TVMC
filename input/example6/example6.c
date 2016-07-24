int y = 10;

BEGIN_PROGRAM
int main()
{
  y = y-1;
  
  if (y>5)
  {
    ERROR: ;
  }

END: ;
}

END_PROGRAM