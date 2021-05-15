;gerador

Add_random:
	MOV R0, random
	MOV R1, [R0]
	ADD R1, 1
	MOV random, R1
	MOV R0, R1
	
Random:
	MOV R0, random
	MOV R1, [R0]
	MOV R2, R1
	MOD R1, 2
	MOD R2, 3