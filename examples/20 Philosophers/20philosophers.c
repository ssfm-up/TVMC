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
mutex a2 = -1;
mutex b2 = -1;
mutex c2 = -1;
mutex d2 = -1;
mutex e2 = -1;
mutex f2 = -1;
mutex g2 = -1;
mutex h2 = -1;
mutex i2 = -1;
mutex j2 = -1;


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
        acquire a2;
        release j;
        release a2;
    }
}

END_PROGRAM





BEGIN_PROGRAM /*Philosopher 10*/

int main() {
    while(true) {
        acquire a2;
    WAIT:   acquire b2;
        release a2;
        release b2;
    }
}

END_PROGRAM


BEGIN_PROGRAM /*Philosopher 11*/

int main() {
    while(true) {
        acquire b2;
        acquire c2;
        release b2;
        release c2;
    }
}

END_PROGRAM


BEGIN_PROGRAM /*Philosopher 12*/

int main() {
    while(true) {
        acquire c2;
        acquire d2;
        release c2;
        release d2;
    }
}

END_PROGRAM


BEGIN_PROGRAM /*Philosopher 13*/

int main() {
    while(true) {
        acquire d2;
        acquire e2;
        release d2;
        release e2;
    }
}

END_PROGRAM

BEGIN_PROGRAM /*Philosopher 14*/

int main() {
    while(true) {
        acquire e2;
        acquire f2;
        release e2;
        release f2;
    }
}

END_PROGRAM




BEGIN_PROGRAM /*Philosopher 15*/

int main() {
    while(true) {
        acquire f2;
        acquire g2;
        release f2;
        release g2;
    }
}

END_PROGRAM


BEGIN_PROGRAM /*Philosopher 16*/

int main() {
    while(true) {
        acquire g2;
        acquire h2;
        release g2;
        release h2;
    }
}

END_PROGRAM


BEGIN_PROGRAM /*Philosopher 17*/

int main() {
    while(true) {
        acquire h2;
        acquire i2;
        release h2;
        release i2;
    }
}

END_PROGRAM


BEGIN_PROGRAM /*Philosopher 18*/

int main() {
    while(true) {
        acquire i2;
        acquire j2;
        release i2;
        release j2;
    }
}

END_PROGRAM

BEGIN_PROGRAM /*Philosopher 19*/

int main() {
    while(true) {
        acquire j2;
        acquire a;
        release j2;
        release a;
    }
}

END_PROGRAM