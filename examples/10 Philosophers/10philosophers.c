mutex a = -1;
mutex b = -1;
mutex c = -1;
mutex d = -1;
mutex e = -1;
mutex f = -1;
mutex g = -1;
mutex h = -1;
mutex i = -1;
mutex j = -1;


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
        acquire d;
        release c;
        release d;
    }
}

END_PROGRAM


BEGIN_PROGRAM /*Philosopher 3*/

int main() {
    while(true) {
        acquire d;
        acquire e;
        release d;
        release e;
    }
}

END_PROGRAM

BEGIN_PROGRAM /*Philosopher 4*/

int main() {
    while(true) {
        acquire e;
        acquire f;
        release e;
        release f;
    }
}

END_PROGRAM




BEGIN_PROGRAM /*Philosopher 5*/

int main() {
    while(true) {
        acquire f;
        acquire g;
        release f;
        release g;
    }
}

END_PROGRAM


BEGIN_PROGRAM /*Philosopher 6*/

int main() {
    while(true) {
        acquire g;
        acquire h;
        release g;
        release h;
    }
}

END_PROGRAM


BEGIN_PROGRAM /*Philosopher 7*/

int main() {
    while(true) {
        acquire h;
        acquire i;
        release h;
        release i;
    }
}

END_PROGRAM


BEGIN_PROGRAM /*Philosopher 8*/

int main() {
    while(true) {
        acquire i;
        acquire j;
        release i;
        release j;
    }
}

END_PROGRAM

BEGIN_PROGRAM /*Philosopher 9*/

int main() {
    while(true) {
        acquire j;
        acquire a;
        release j;
        release a;
    }
}

END_PROGRAM