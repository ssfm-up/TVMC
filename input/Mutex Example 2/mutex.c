mutex a = -1;
mutex b = -1;
mutex c = -1;
mutex d = -1;
mutex e = -1;
mutex f = 0;

/*
EF((pc_1=WAIT1) /\ (pc_2=WAIT2))
*/
 
 
BEGIN_PROGRAM
 
int main() {
    while(true) {
        	 acquire a;
        	 acquire b;
        	 acquire c;
        	 release c;
        	 release b;
        	 release a;
        	 release f;
        	 acquire f;
    }
}
 
END_PROGRAM

 
BEGIN_PROGRAM
 
int main() {
    while(true) {
        	 acquire c;
        	 acquire d;
        	 release d;
        	 release c;
        	 acquire a;
      WAIT1: acquire e;
        	 release e;
        	 release a;  
    }
}
 
END_PROGRAM


BEGIN_PROGRAM
 
int main() {
    while(true) {
        	 acquire e;
        	 acquire f;
      WAIT2: acquire a;
        	 release a;
        	 release f;
        	 release e;
    }
}
 
END_PROGRAM