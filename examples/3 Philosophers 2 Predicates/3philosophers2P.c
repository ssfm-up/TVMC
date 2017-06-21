mutex a = -1;
mutex b = -1;
mutex c = -1;

/*Check for: F(G(pc_0=WAIT)) under fairness, for b = 1,2,3,..., until both formulas (unknown and not unknown) are satisfiable */
 
BEGIN_PROGRAM /*Philosopher 0*/

int main() {
    while(true) {
            acquire a;
    WAIT:   acquire b;
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
        acquire c;
        acquire a;
        release c;
        release a;
    }
}

END_PROGRAM