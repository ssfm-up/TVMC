mutex a = -1;
mutex b = -1;

/*Check for: F(G(pc_0=WAIT)) under fairness, for b = 1,2,3,..., until both formulas (unknown and not unknown) are satisfiable */
 
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
            acquire a;
            release b;
            release a;
    }
}

END_PROGRAM