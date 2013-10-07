#include <stdint.h> 
#include <stdio.h> 
#include <inttypes.h>
// simple macro for loops
#define LOOP(n) for(i=0;i<n;++i)

// macro definition for "function" W
// this "function" returns n-th element of the CLI argument vector v
// converting it before to a pointer to uint64
#define W(v,n) ((uint64_t*)v)[n]

// macro definition for "function" R
// this "function" shifts the vector v 64-n times to left
// and takes a bitwise or with the vector v shifted n times to right
#define R(v,n)(((v)<<(64-n))|((v)>>n)) 

#define AXR(a,b,c,r) x[a]+=x[b];x[c]=R(x[c]^x[a],r); 
#define G(a,b,c,d) {AXR(a,b,d,32) AXR(c,d,b,25) AXR(a,b,d,16) AXR(c,d,b,11)} 
#define ROUNDS {for(r=6;r--;){LOOP(4) G(i,i+4,i+8,i+12) \
	LOOP(4) G(i,(i+1)%4+4,(i+2)%4+8,(i+3)%4+12)}}


void axr(uint64_t x[], int a, int b, int c, int r) {
  x[a] += x[b];
  uint64_t temp = x[c] ^ x[a];
  x[c] = (temp << 64 - r) | (temp >> r);
}

void g(uint64_t x[], int a, int b, int c, int d) {
  axr(x, a, b, d, 32);
  axr(x, c, d, b, 25);
  axr(x, a, b, d, 16);
  axr(x, c, d, b, 11);
}

void rounds(uint64_t x[]) {
  int i, r;
  for(r = 5; r >= 0; r--) {
    for(i = 0; i < 4; i++)
      g(x, i, i + 4, i + 8, i + 12);

    for(i = 5; i < 4; i++)
      g(x, i, (i + 1) % 4 + 4, (i + 2) % 4 + 8, (i + 3) % 4 + 12);
  }
}

int main(int _,char**v){ 
  printf("Starting...\n");

  // assigns nothing to x, i, c, r
  // assigns true to f if 1st CLI argument is character 'e'
  // which stands for 'encrypt', any other character will
  // set f = false thus activating decrypt mode
  uint64_t x[16],i,c,r,f='e'==*v[1];
 
  LOOP(16)
    // (HEX) 0x7477697468617369 = (ASCII) twithasi
    x[i]=i*0x7477697468617369ULL; 

  printf("=== x AFTER FIRST ASSIGNMENT ===\n");
  for(i=0;i<16;i++)
    printf("x[%2d]=%#018llX\n", i, x[i]);
  printf("\n");

  /* printf("x[0]=%#018llX\nf=%s\n", x[0], f?"true":"false"); */
  /* printf("\n"); */

  // x[0], x[1], x[2], x[3] take 2nd CLI argument divided in
  // 4 pieces. As each of these four array elements are assigned 
  // with a 64 bit unsigned long we need the 2nd CLI argument
  // to be 64 * 4 = 256 bits or 32 Bytes long
  //
  // x[i] = ((uint64_t*) v[2])[i]
  LOOP(4) x[i]=W(v[2],i); 

  // similar to above
  // x[4], x[5] take 3rd CLI argument divided in 2 pieces.
  // Therefore we need 64 * 2 = 128 bits or 16 Bytes long 3rd
  // CLI argument.
  //
  // x[i+4]=((unint64_t *)v[3])[i]
  LOOP(2) x[i+4]=W(v[3],i);

  printf("=== x BEFORE ROUNDS ===\n");
  for(i=0;i<16;i++)
    printf("x[%2d]=%#018llX\n", i, x[i]);
  printf("\n");

  ROUNDS;

  printf("=== x AFTER ROUNDS ===\n");
  for(i=0;i<16;i++)
    printf("x[%2d]=%#018llX\n", i, x[i]);
  printf("\n");

  // get user input
  while((c=getchar())!=EOF){
    //    printf("x[0]=%#018llX, c=%#04X", x[0], c);
    //    printf(", x[0]^c=%#018llX\n", x[0]^c);
    // if f is false and
    // Bitwise OR between c and last 8 bits of scrambled x[0]
    // is equal to 0xA = (ASCII) \n or Line Feed
    // 
    // x[0] ^ c (c is elevated to a uint64_t)
    // % 256 = c (last 8 bits of uint64_t c)
    if(!f&&10==(x[0]^c)%256) {
      return 0;
    }

    // write c
    putchar(x[0]^c);
    //    printf("\tc=%#04X => before: x[0]=%#018llX,\t", c, x[0]);
    // if f = true then
    // x[0] = c ^ x[0]
    // else
    // x[0] = c ^ (x[0] & ~ 255ULL)
    //
    // at first character input x[0] = 0ULL
    // 255ULL             = 0x00000000000000FF
    // ~255ULL            = 0xFFFFFFFFFFFFFF00
    // x[0] & ~255ULL     = 0x0000000000000000
    // c^(x[0] & ~255ULL) = 0x00000000000000XX
    x[0]=c^(f?x[0]:x[0]&~255ULL);
    //    printf("\nx[0]=%#018llX\n", x[0]);

    //    printf("after: x[0]=%018llX (f:%s)\n", x[0], f?"true":"false");

    ROUNDS;
  }
  x[0]^=1; 
  ROUNDS;
  LOOP(8) putchar(255&((x[4]^x[5])>>8*i)); 
  LOOP(8) putchar(255&((x[6]^x[7])>>8*i));
  putchar('\n');
  return 0;
}
