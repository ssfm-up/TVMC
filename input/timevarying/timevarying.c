int block = 0;
bool busy = false;
bool inode = false;
mutex m_inode = -1;
mutex m_busy = -1;

BEGIN_PROGRAM
int main() {
	acquire m_inode;
	if(!inode) {
		acquire m_busy;
		busy = true;
		release m_busy;
		inode = true;
	}
	block = 1;
ASS1: ; /*block = 1*/
	release m_inode;
}
END_PROGRAM

BEGIN_PROGRAM
int main() {
	acquire m_busy;
	if (!busy) {
		block = 0;
ASS2:	; /*block = 0*/
	}
	release m_busy;
}
END_PROGRAM