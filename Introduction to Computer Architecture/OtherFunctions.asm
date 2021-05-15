;|---------------------------------------------|
;| Verifica se ha colisoes                     |
;|---------------------------------------------|
;|-----------------------------------|
;| ColisaoMissil                     |
;|-----------------------------------|
ColisaoMissil:
	MOV R0, linhaAST
	MOV R1, colunaAST
	ADD R0, R6
	ADD R1, R6
	MOV R0, [R0]
	MOV R1, [R1]
	ADD R0, 4
	CMP R0, linhaMissil
	JNZ saicolisaoMissil
	CMP R1, colunaMissil
	JLT saicolisaoMissil
	ADD R1, 4
	CMP R1, colunaAST
	JGT saicolisaoMissil
	CALL destroiAst
	CALL desligaMissil
saicolisaoMissil:
	RET
	
destroiAst:
	CALL apagaAsteroide
	MOV R0, asteroideExpl
	CALL escrevetab
	RET
	
;|-----------------------------------|
;| ColisaoNave                       |
;|-----------------------------------|
ColisaoNave:
	MOV R4, 23
	MOV linhaAtual
	CMP linhaAtual, R4
	JLT saiColisaoNave
	
	cicloColisoes:
		CMP colunaD, R3
		JLT saiColisaoNave
		CMP colunaL, R2
		JGT saiColisaoNave
		ADD R3, 1
		SUB R2, 1
		CMP R3, 5
		JZ 
		JMP haColisao
	haColisao:
		MOV R1, tiposAST
		ADD R1, R6
		MOV R1, [R1]
		MOV R2, AsteroidesBons
		CMP R1, R2
		JNZ colisaoMau
		
colisaoBom:
	CALL addScore
	JMP saiColisaoNave
		
colisaoMau:
	JMP fim
	
saiColisaoNave:
	RET

;|-----------------------------------|
;| mexeAst                           |
;|-----------------------------------|	
mexeAst:
	PUSH R0
	PUSH R1
	PUSH R2	
	MOV R0, colunaAST
	MOV R1, 4
	
	cicloMexeAst:
		MOV R2, [R0] ; coluna atual
		ADD R2, R4
		MOV [R0], R2 ; coluna após mexer
		
		ADD R0, 2
		SUB R1, 1
		JZ saiMexeAst
	
	saiMexeAst:
		POP R2
		POP R1
		POP R0
		RET

		
addColunaLinha:
	PUSH R0
	PUSH R1
	PUSH R2
	PUSH R3
	PUSH R4
	PUSH R5
	MOV R1, colunaAST
	MOV R5, linhaAST
	MOV R0, direcaoAST
	MOV R4, 4
	cicloAddColunaLinha:
		MOV R2, [R1]
		MOV R3, [R0]
		ADD R2, R3
		MOV [R1], R2
		ADD R0, 1
		ADD R1, 1
		SUB R4, 1
		JZ saiAddColunaLinha
		JMP cicloAddColunaLinha
	saiAddColunaLinha:
		POP R5
		POP R3
		POP R2
		RET