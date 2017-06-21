mutex a = -1;
mutex b = -1;
mutex c = -1;

/*Check for: F(G(pc_0=WAIT)) under fairness, for b = 1,2,3,...,10,... both formulas (unknown and not unknown) should be never satisfiable at the same time */
 
BEGIN_PROGRAM /*Philosopher 0*/
 
int main() {
    while(true) {
        	 acquire a;
      WAIT:  acquire b;
        	 release a;
        	 release b;
    }
}
 
END_PROGRAM


BEGIN_PROGRAM /*Philosopher 1*/

int main() {
    while(true) {
            acquire b;
            acquire c;
            release b;
            release c;
    }
}

END_PROGRAM

BEGIN_PROGRAM /*Philosopher 2*/

int main() {
    while(true) {
        acquire a;
        acquire c;
        release a;
        release c;
    }
}

END_PROGRAM