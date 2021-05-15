;Grupo 04
;Alunos:                     Numero de aluno:
;Maria Beatriz Venceslau      93734
;Diogo Silva                  93706
;Martim Pimentel              93738

; |---------------------------------------------------------------------|
; | Constantes                                                          |
; |---------------------------------------------------------------------|
DISPLAYS       EQU 0A000H  ; endereço dos displays de 7 segmentos (periférico POUT-1)
TEC_LIN        EQU 0C000H  ; endereço das linhas do teclado (periférico POUT-2)
TEC_COL        EQU 0E000H  ; endereço das colunas do teclado (periférico PIN)
LINHA          EQU 8       ; linha a testar (4ª linha, 1000b)
ECRA           EQU 8000H   ; endereço do ecrã (pixelscreen)
BYTES          EQU 128
FimAsteroidesE EQU 0FFFBH  ; Coluna onde os asteroides dão respawn do lado esquerdo
FimAsteroidesD EQU 36      ; Coluna onde os asteroides dão respawn do lado direito
LimiteDireita  EQU 32      ; Limite de escrita do lado direito do escreveTab
LimiteEsquerda EQU 0       ; Limite de escrita do lado esquerdo do escreveTab
nAsteroides    EQU 4


PLACE       1000H
;|--------------------------------------------------------------------------------------------------|
;|  Tabelas dos objetos a desenhar                                                                  |
;|  label:      STRING  nº linhas da tabela,nº de colunas da tabela, linha inicial, coluna inicial  |
;|              STRING  objeto em binário                                                           |
;|--------------------------------------------------------------------------------------------------|
NAVE:      	 STRING 32,5,27,0
			 STRING 0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0
			 STRING 0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0
			 STRING 0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0
			 STRING 0,1,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1,0
			 STRING 1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1

VOLANTENEUTRO: STRING 4,3,29,14
			   STRING 0,0,0,0
			   STRING 1,1,1,1
			   STRING 0,0,0,0


VOLANTEDIR:		STRING 4,3,29,14
				STRING 1,0,0,0
				STRING 0,1,1,0
				STRING 0,0,0,1

VOLANTEESQ: 	 STRING 4,3,29,14
				 STRING 0,0,0,1
				 STRING 0,1,1,0
				 STRING 1,0,0,0
				 
;|--------------------------------------------------------------------------------------------------|
;|  Tabelas dos objetos a desenhar                                                                  |
;|  label:      WORD  nº colunas da tabela                                                          |
;|              WORD  nº de linhas da tabela                                                        |
;|              WORD  coluna inicial                                                                |
;| 				WORD  linha inicial                                                                 |
;|              STRING  objeto em binário                                                           |
;|--------------------------------------------------------------------------------------------------|
Asteroide1:	   WORD   1
			   WORD   1
			   STRING 1

Asteroide2:	   WORD   2
			   WORD   2
			   STRING 1,1
			   STRING 1,1

Asteroide3Bom: WORD   3
			   WORD   3
			   STRING 1,0,1
			   STRING 0,1,0
			   STRING 1,0,1

Asteroide4Bom: WORD   4
			   WORD   4
			   STRING 1, 0, 0, 1
			   STRING 0, 1, 1, 0
			   STRING 0, 1, 1, 0
			   STRING 1, 0, 0, 1

Asteroide5Bom: WORD   5
			   WORD   5
			   STRING 1,0,0,0,1
			   STRING 0,1,0,1,0
			   STRING 0,0,1,0,0
			   STRING 0,1,0,1,0
			   STRING 1,0,0,0,1

Asteroide3Mau: WORD   3
			   WORD   3
			   STRING 0,1,0
			   STRING 1,1,1
			   STRING 0,1,0


Asteroide4Mau: WORD   4
			   WORD   4
			   STRING 0, 1, 1, 0
			   STRING 1, 0, 1, 1
			   STRING 1, 1, 0, 1
			   STRING 0, 1, 1, 0

Asteroide5Mau: WORD   5
			   WORD   5
			   STRING 0,1,1,1,0
			   STRING 1,0,1,0,1
			   STRING 1,1,0,1,1
			   STRING 1,0,1,0,1
			   STRING 0,1,1,1,0
			   
AsteroideExpl:  WORD 5
				WORD 5
				STRING 0,1,0,1,0
				STRING 1,0,1,0,1
				STRING 0,1,0,1,0
				STRING 1,0,1,0,1
				STRING 0,1,0,1,0

AsteroidesBons:	WORD Asteroide1
				WORD Asteroide2
				WORD Asteroide3Bom
				WORD Asteroide4Bom
				WORD Asteroide5Bom

AsteroidesMaus: WORD Asteroide1
				WORD Asteroide2
				WORD Asteroide3Mau
				WORD Asteroide4Mau
				WORD Asteroide5Mau
				
TAB_MISSIL: WORD 1
			WORD 1
			WORD COLUNA_MISSIL
			WORD LINHA_MISSIL
			STRING 1
			
COLUNA_MISSIL: WORD 15
LINHA_MISSIL:  WORD 27
DIRECAO_MISSIL: WORD 0
				
probAST: 		WORD AsteroidesBons
				WORD AsteroidesMaus
				WORD AsteroidesMaus
				WORD AsteroidesBons
				WORD AsteroidesMaus
				WORD AsteroidesMaus
				WORD AsteroidesMaus
				WORD AsteroidesMaus
				WORD AsteroidesBons
				WORD AsteroidesMaus

interrupcoes:	WORD rot_int_0			; interrupção de animação de barras
				WORD rot_int_1

checkInterrupt:	STRING 0                ; Interrupção dos asteroides

checkinterrupt1: STRING 0				; Interrupção do míssil


asteroide1:	WORD 0 ; estado do Asteroide
			WORD 0 ; endereço do desenho
			WORD 0 ; coluna
			WORD 0 ; linha
			WORD 0 ; tipo Asteroide
			WORD 0 ; direção
			WORD 0 ; Direção Volante
			
asteroide2:	WORD 0 ; estado do Asteroide
			WORD 0 ; endereço do desenho
			WORD 0 ; coluna
			WORD 0 ; linha
			WORD 0 ; tipo Asteroide
			WORD 0 ; direção
			WORD 0 ; Direção Volante
			
asteroide3:	WORD 0 ; estado do Asteroide
			WORD 0 ; endereço do desenho
			WORD 0 ; coluna
			WORD 0 ; linha
			WORD 0 ; tipo Asteroide
			WORD 0 ; direção
			WORD 0 ; Direção Volante
			
asteroide4:	WORD 0 ; estado do Asteroide
			WORD 0 ; endereço do desenho
			WORD 0 ; coluna
			WORD 0 ; linha
			WORD 0 ; tipo Asteroide
			WORD 0 ; direção
			WORD 0 ; Direção Volante

asteroides: WORD asteroide1
			WORD asteroide2
			WORD asteroide3
			WORD asteroide4
			
TECLA_PREMIDA:	WORD 0

ESTADO_PAUSA_INICIO: WORD 00H

SCORE: WORD 00H

TECLA_ANTIGA: WORD 00H

TECLA_NOVA: WORD 00H

estadoINIT:     STRING 0

TURN_ON_MISSIL: STRING 00H

ESTADO_INICIALIZACAO: STRING 00H

random:     WORD    0

mascara:    STRING  80H
			STRING  40H
			STRING  20H
			STRING  10H
			STRING  8H
			STRING  4H
			STRING  2H
			STRING  1H

;|-------------------------------|
;| Ecrãs                         |
;|-------------------------------|

ECRA_I:	
			STRING 00H,00H,00H,00H
			STRING 00H,00H,00H,00H
			STRING 00H,00H,00H,00H
			STRING 00H,00H,00H,00H
			STRING 00H,00H,00H,00H
			STRING 00H,00H,00101000b,00H
			STRING 00H,00H,00010000b,00H
			STRING 00H,00H,00101000b,00H
			STRING 00H,00H,00H,00H
			STRING 00H,00H,00H,00H
			STRING 00110010b,01000100b,11010010b,01001000b
			STRING 01000101b,01000101b,00010101b,01101000b
			STRING 01000101b,01000100b,10010101b,01011000b
			STRING 01000101b,01000100b,01010101b,01001000b
			STRING 00110010b,01110101b,10010010b,01001000b
			STRING 00H,00H,00H,00H
			STRING 00H,00H,00H,00H
			STRING 00H,00H,00H,10001000b
			STRING 00H,00H,00H,01010000b
			STRING 00H,00000010b,10000000b,00100000b
			STRING 00H,00000101b,01000000b,01010000b
			STRING 00H,00000010b,10000000b,10001000b
			STRING 00H,00000101b,01000000b,00H
			STRING 00H,00000010b,10000000b,00H
			STRING 00H,00H,00H,00H
			STRING 00H,00H,00H,00H
			STRING 00H,00H,00H,00H
			STRING 00001111b,11111111b,11111111b,11110000b
			STRING 00010000b,00H,00H,00001000b
			STRING 00100000b,00H,00H,00000100b
			STRING 01000000b,00000011b,11000000b,00000010b
			STRING 10000000b,00H,00H,00000001b
		
ECRA_GANDA_JOGADOR:
	STRING 00000000b,00000000b,00000000b,00000000b
	STRING 00000000b,00000000b,00000010b,00000000b
	STRING 00000000b,00000000b,00000000b,00000000b
	STRING 01100100b,11000100b,11001110b,10100110b
	STRING 01011010b,10101010b,10101000b,10101000b
	STRING 01101110b,11001110b,11001100b,11101100b
	STRING 01001010b,10101010b,10101000b,10100010b
	STRING 01001010b,10101010b,11001110b,10101100b
	STRING 00000000b,00000000b,00000000b,00000000b
	STRING 00000000b,00000000b,00000000b,00000000b
	STRING 00000011b,00010010b,01011000b,10000000b
	STRING 00000100b,00101011b,01010101b,01000000b
	STRING 00000101b,10111010b,11010101b,11000000b
	STRING 00000101b,00101010b,01010101b,01000000b
	STRING 00000011b,00101010b,01011001b,01000000b
	STRING 00000000b,00000000b,00000000b,00000000b
	STRING 00000000b,00000000b,00000000b,00000000b
	STRING 00111001b,00011000b,10011000b,10011000b
	STRING 00001010b,10100001b,01010101b,01010100b
	STRING 00001010b,10101101b,11010101b,01011000b
	STRING 00101010b,10101001b,01010101b,01010100b
	STRING 00010001b,00011001b,01011000b,10010100b
	STRING 00000000b,00000000b,00000000b,00000000b
	STRING 00000000b,00000000b,00000000b,00000000b
	STRING 00000000b,00001110b,11100000b,00000000b
	STRING 00000000b,00001010b,10100000b,00000000b
	STRING 00000000b,00001110b,11100000b,00000000b
	STRING 00000000b,00000010b,00100000b,00000000b
	STRING 00000000b,00000010b,00100000b,00000000b
	STRING 00000000b,00000010b,00100000b,00000000b
	STRING 00000000b,00000000b,00000000b,00000000b
	STRING 00000000b,00000000b,00000000b,00000000b	
	
ECRA_GAME_OVER: 
			 STRING 3H,80H,3H,80H
			 STRING 2H,80H,2H,80H
			 STRING 0CH,80H,2H,60H
			 STRING 8H,5FH,0F4H,20H
			 STRING 0EH,60H,0CH,0E0H
			 STRING 1H,0C0H,07H,0H
			 STRING 0H,80H,02H,0H
			 STRING 01H,18H,31H,0H
			 STRING 01H,34H,69H,0H
			 STRING 01H,3CH,79H,0H
			 STRING 01H,19H,31H,0H
			 STRING 01H,23H,81H,0H
			 STRING 0H, 83H,82H,0H
			 STRING 0H,0E0H,0EH,0H
			 STRING 1H,30H,19H,0H
			 STRING 0EH, 28H,28H,0E0H
			 STRING 08H, 48H,24H,20H
			 STRING 0CH, 8FH, 0E2H, 60H
			 STRING 2H,80H,2H,80H
			 STRING 3H,80H,3H,80H
			 STRING 0H,0H,0H,0H
			 STRING 0F3H,14H,0E0H,0H
			 STRING 84H,0AAH,80H,0H
			 STRING 0B7H,0A2H,0C0H,0H
			 STRING 94H,0A2H,80H,0H
			 STRING 0F4H,0A2H,0E0H,0H
			 STRING 0H,0H,0H,0H
			 STRING 0H,7H,0ABH,0BCH
			 STRING 0H,4H,0AAH,24H
			 STRING 0H,4H,0ABH,0BCH
			 STRING 0H,4H,0AAH,28H
			 STRING 0H,7H,93H,0A4H
			 
colunaEsq:	WORD 5
			WORD 4
			WORD 3
			WORD 2
			WORD 1
			WORD 0
colunaDir:	WORD 27
	        WORD 28
			WORD 29
			WORD 30
			WORD 31
			 
;|--------------------------|
;|  Funções teclas          |
;|--------------------------|

TECLAS_NAVE:     WORD tecla_inutil
				 WORD tecla_0
				 WORD tecla_inutil
				 WORD tecla_inutil
				 WORD tecla_3
				 WORD tecla_inutil
				 WORD tecla_inutil
				 WORD tecla_inutil
				 WORD tecla_inutil
				 WORD tecla_inutil
				 WORD tecla_inutil
				 WORD tecla_inutil
				 WORD tecla_inutil
				 WORD tecla_inutil
				 WORD tecla_inutil
				 WORD tecla_inutil
				 WORD tecla_inutil
				 
TECLAS_CONTROLO: WORD nao_ha_tecla
				 WORD tecla_inutil
				 WORD tecla_1
				 WORD tecla_inutil
				 WORD tecla_inutil
				 WORD tecla_inutil
				 WORD tecla_inutil
				 WORD tecla_inutil
				 WORD tecla_inutil
				 WORD tecla_inutil
				 WORD tecla_inutil
				 WORD tecla_inutil
				 WORD tecla_inutil
				 WORD tecla_C
				 WORD tecla_D
				 WORD tecla_inutil
				 WORD tecla_inutil
				 

pilha:      TABLE 100H					; espaço reservado para a pilha
										; (200H bytes, pois são 100H words)
SP_inicial:								; este é o endereço (1200H) com que o SP deve ser


										; inicializado. O 1.º end. de retorno será
										; armazenado em 11FEH (1200H-2)
	

PLACE       0H
inicio:
	 MOV  BTE, interrupcoes
     MOV  SP, SP_inicial
	 EI0
	 EI1
	 EI
	 CALL limpaEcra
	 MOV  R7, ECRA_I    
	 CALL print_ecra_inicial     ; Imprime ecra principal 
	 PUSH R4
	 MOV  R4, DISPLAYS           ; endereço do periférico dos displays
	 MOV R1, 0           
     MOVB [R4], R1               ; inicia-se o valor a zeros dos displays
	 POP R4

;fim:
	 
cicloPrincipal:
	 CALL teclado

	 CALL funcControlo
	 
	 MOV R7, ESTADO_PAUSA_INICIO     ; Verifica se o programa esta em pausa.
	 MOV R7,[R7]
	 CMP R7, 1
	 JZ cicloPrincipal               ; Se sim, salta para o ciclo principal
	 
	 MOV R9, ESTADO_INICIALIZACAO    ; Verifica se estamos no ecra inicial.
	 MOVB R8,[R9]
	 CMP R8, 1
	 JNZ cicloPrincipal	 			 ; Se sim, apenas podemos clicar na tecla C, 
									 ; por isso salta para o ciclo principal
	 CALL funcNave          		 ; Rotina que controla as teclas 1 e 3
	 CALL updateAsteroide
	 CALL missil            		 ; Rotina que controla o missil
	 CALL Add_random
	 JMP  cicloPrincipal
;	 JMP fim

;|-------------------------------|
;| EXCECOES                      |
;|-------------------------------|
rot_int_0:
	 PUSH R0
	 PUSH R1
	 MOV  R0, checkInterrupt
	 MOV  R1,1
	 MOVB  [R0],R1
	 POP  R1
	 POP  R0
	 RFE

rot_int_1:
	 PUSH R0
	 PUSH R1
	 MOV R0, checkinterrupt1
	 MOV R1, 1
	 MOVB [R0],R1
	 POP R1
	 POP R0
	 RFE

;|----------------------------------------------------------------|
;| PROCESSOS                                                      |
;|----------------------------------------------------------------|
;|------------------------------------------------------------------------|
;| PROCESSO : updateAsteroide                                             |
;| DESCRICAO: desce uma linha e adiciona uma direção se houver interrupção|
;|------------------------------------------------------------------------|
updateAsteroide:
	 PUSH R0
	 PUSH R1
	 PUSH R2
	 PUSH R3
	 PUSH R4
     MOV  R0, checkInterrupt				; Verifica se houve alguma interrupção
	 MOVB R1,[R0]
	 CMP  R1, 0
	 JZ   saiUpdateAsteroide				; se não houve não desce uma linha os asteroides
	 
	 MOV R3,nAsteroides
	 MOV R2,asteroides
	 cicloUpdate:
		 MOV R1,[R2]
		 MOV R0,[R1]
		 CMP R0,2
		 JZ estadoMorto
		 CMP R0,0
		 JZ callReset
		 CALL apagaAsteroide
		 CALL addColunaLinha
		 CALL updateTamanho
		 CALL desenhaAsteroide
		 CALL ColisaoAstNave
		 CALL colisaoAstMissil
		 JMP  proximoAsteroide
	callReset:
		 CALL Reset
		 JMP proximoAsteroide
	estadoMorto:
		 MOV R4,0
		 MOV [R1],R4
		 CALL apagaAsteroide
		 JMP proximoAsteroide
	proximoAsteroide:
		 ADD R2,2
		 SUB R3,1
		 JZ resetInt
		 JMP cicloUpdate
	 resetInt:	 
		MOV  R1,checkInterrupt					; Volta a por o evento de interrupção a 0
		MOV  R0,0
		MOVB  [R1], R0
	 
	 saiUpdateAsteroide:
		POP R4
		POP R3
		POP R2
		POP R1
		POP R0
		RET		
;|--------------------------------------------------------------------------------|
;|	ROTINA: teclado														          |
;| 	DESCRICAO: nesta rotina, verifica-se se se clicou em alguma tecla e			  |
;|  armazena-se na memoria, senao mete -1 na memoria.                             |
;|	INPUT:	none																  |	 
;|	OUTPUT:	tecla na memoria													  |
;|--------------------------------------------------------------------------------|

teclado:     
	PUSH R0
	PUSH R1
	PUSH R2
	PUSH R3
	PUSH R5
	PUSH R6
	PUSH R7
	PUSH R8
	MOV  R1, LINHA     		; Valor da 4 linha
	MOV  R2, TEC_LIN   		; endereço do periférico das linhas
    MOV  R3, TEC_COL   		; endereço do periférico das colunas
  varrimento:	       		; Varre as 4 linhas
    MOVB [R2], R1      		; escrever no periférico de saída (linhas)
    MOVB R0, [R3]      		; ler do periférico de entrada (colunas)
    CMP  R0, 0         		; há tecla premida? 
    JNZ   mudancas     		; Se ha tecla premida, salta para mudancas
	SHR  R1, 1         		; mudança de linha
    CMP  R1, 0        		; vê se a linha é 0(impossivel) 
    JNZ varrimento          ; Se ainda nao e linha invalida, corre-a
							; Senao armazena-se -1 na memoria	 
	MOV  R6, TECLA_PREMIDA
	MOV  R5, -1          	; mete um valor simbolico invalido para dar "erro"
	MOV  [R6], R5
	JMP saiTeclado

  mudancas:					; inicializações
     MOV  R6, R1        	; R1 vai ser alterado por isso armazena-se o valor da linha em R6
     MOV  R7, -1        
     MOV  R8 , -1
	 
  mudanca_numLinha:     	; conta-se o numero de SHR que a linha precisa de dar para chegar a zero.
     SHR  R6, 1
     ADD  R7, 1
     CMP  R6, 0
     JNZ  mudanca_numLinha
	 
  mudanca_numColuna:    	; conta-se o numero de SHR que a coluna precisa de dar para chegar a zero.
	 SHR  R0, 1
     ADD  R8, 1
     CMP  R0, 0
     JNZ  mudanca_numColuna
    
     SHL R7, 2          	; SHL por 2 é multiplicar por 4
     ADD R7, R8         	; tecla= 4*linha + coluna
	 
	 MOV  R5, TECLA_PREMIDA ; armazena-se o valor da tecla premida na memoria
	 MOV  [R5], R7                                                                          
	 JMP  saiTeclado
	 
  saiTeclado:
	 POP R8
	 POP R7
	 POP R6
	 POP R5
	 POP R3
	 POP R2
	 POP R1
	 POP R0
	 RET

;|--------------------------------------------------------------------------------|
;|	ROTINA: funcControlo														  |
;| 	DESCRICAO: Nesta rotina, verifica-se se a tecla premida no ciclo anterior é   |
;|	igual à deste momento.Se sim, sai da funcao, senao:se se esta no ecra 		  |
;|	principal(ESTADO_INICIALIZACAO a 0) chama a rotina espera_inicio, senao 	  |
;|	chama-se a rotina correspondente a tecla. Esta rotina so executa ações        |
;|  visiveis ao carregar nas teclas C,D ou 1.									  |
;|	INPUT:	none																  |	 
;|	OUTPUT:	Ação correspondente a tecla premida.	     						  |
;|--------------------------------------------------------------------------------|
funcControlo:
	 PUSH R5
	 PUSH R6
	 PUSH R7
	 PUSH R8
	 PUSH R9
	 
	 MOV R8, TECLA_ANTIGA
	 MOV R9, TECLA_PREMIDA
	 MOV R7, [R8]
	 MOV R6, [R9]
	 CMP R6, R7              		; Compara a igualdade da tecla premida neste ciclo com ciclo imediatamente anterior
	 JZ sai_funcControlo	 		; Se forem iguais, sai da rotina
	 MOV R8, TECLA_ANTIGA    		; Se nao forem iguais, guarda na tabela TECLA_ANTIGA o valor da tecla 
	 MOV [R8], R6			 		; atual para ser usado no proximo ciclo.
	 JMP prossegue
	 
  prossegue:	 
	 MOV  R6, TECLA_PREMIDA         ; Vai buscar o valor da tecla premida a memoria
	 MOV  R5, [R6]                                                                                
	 
	 MOV R9, ESTADO_INICIALIZACAO   ; Verifica se estamos dentro do jogo(bit de estado a 1) 
	 MOVB R8,[R9]					; ou no ecra inicial(bit de estado a 0)
	 CMP R8, 1
	 JZ entra_func_teclado	        ; Se estamos dentro do jogo, pode correr o valor da tecla que premimos
	 CALL espera_inicio		 	    ; Senao, chama a rotina espera_inicio que verifica se se premiu a tecla C(inicia o jogo)
	 JMP sai_funcControlo	        ; Ao retornar da rotina, sai da rotina funcControlo
	 
  entra_func_teclado:	 
	 MOV  R6, TECLA_PREMIDA         ; Vai buscar o valor da tecla premida a memoria
	 MOV  R5, [R6]
	 
	 SHL R5,1	                    ; multiplica-se o valor da tecla premida(ou nao premida, (-1)) por 2
	 ADD R5,2					    ; Soma se a esse valor 2
	 MOV R6, TECLAS_CONTROLO	
	 ADD R6, R5					    ; Soma do valor da (tecla*2)+2 com o endereço base que da o valor do endereço correspondente a sua tecla
	 MOV R9, [R6]
	 CALL R9	                    ; chama-se a rotina correspodente a tecla premida
	 JMP sai_funcControlo

  sai_funcControlo:
	 POP R9
	 POP R8
	 POP R7
	 POP R6
	 POP R5
	 RET	 
	 
;|--------------------------------------------------------------------------------|
;|	ROTINA: funcNave			    											  |
;| 	DESCRICAO: Nesta rotina, le-se da memoria a tecla premida e chama a rotina 	  |
;|  correspondente a essa tecla. Esta rotina so executa ações visiveis ao carregar|
;|  nas teclas C,D ou 1.														  |   						                    
;|	INPUT:	none																  |	 
;|	OUTPUT:	Acao correspondente a tecla premida									  |
;|--------------------------------------------------------------------------------|
funcNave:
	 PUSH R5
	 PUSH R6
	 PUSH R9
	 
	 MOV  R6, TECLA_PREMIDA        ; Vai buscar o valor da tecla premida a memoria
	 MOV  R5, [R6]                                                                                

	 SHL R5,1	                   ; multiplica-se o valor da tecla premida(ou nao premida, (-1)) por 2
	 ADD R5,2					   ; Soma se a esse valor 2
	 MOV R6, TECLAS_NAVE	
	 ADD R6, R5					   ; Soma do valor da (tecla*2)+2 com o endereço base que da o valor do endereço correspondente a sua tecla
	 MOV R9, [R6]
	 CALL R9	                   ; chama-se a rotina correspodente a tecla premida
	 JMP sai_funcNave

  sai_funcNave:
	 POP R9
	 POP R6
	 POP R5
	 RET
	 
;|--------------------------------------------------------------------------------|
;|	ROTINA: missil 														 		  | 
;|	DESCRICAO: nesta rotina, verifica-se se foi premida a tecla 1 atraves do bit  |
;|  de estado TURN_ON_MISSIL. Se estiver a 1, o missil progride no trajeto, se	  |	
;|  estiver a 0, salta para fora da funcao.	A velocidade aparente do missil é 	  |
;|  coordenado pela interrupcao.												  |
;|  INPUT- None																	  |
;|  OUTPUT- Progressao do missil ou saltar da rotina							  |
;|--------------------------------------------------------------------------------|		
missil:
	 PUSH R0
	 PUSH R1
	 PUSH R2
	 PUSH R3
	 PUSH R4
	 
	 MOV R0, checkinterrupt1	; verifica se a interrupcao esta ativa
	 MOVB R1, [R0]
	 CMP R1, 1					; se nao estiver sai da rotina
	 JNZ sai_missil
	 
	 MOV R3, TURN_ON_MISSIL		; Verifica se se cliclou na tecla 1 atraves do bit de estado a 1 ou 0
	 MOVB R1,[R3]
	 MOV R4, 1
	 CMP R1, R4
	 JNZ sai_missil				; Se estiver a 0, sai da rotina, pois o missil nao foi "ativado"
	 
	 MOV R0, LINHA_MISSIL
	 MOV R1, [R0]				
	 MOV R2, 27
	 CMP R1, R2					; compara se o missil esta na sua linha inicial
	 JZ diminui_linha			; Se for a primeira linha,salta para o ciclo printa que escrevera o bit na linha atual
	 MOV R1, estadoINIT
	 MOV R0, 2
	 MOVB [R1], R0
	 MOV R0, TAB_MISSIL
	 CALL apagaObjeto
	 JMP diminui_linha
	 
diminui_linha:
	 MOV R0, DIRECAO_MISSIL
	 MOV R0, [R0]
	 MOV R1, COLUNA_MISSIL
	 MOV R2, [R1]
	 ADD R2, R0
	 MOV [R1], R2
	 MOV R0, LINHA_MISSIL
	 MOV R1, [R0]				
	 SUB R1, 1				    ; diminui se a linha na tabela que escreve o missil na linha atual 
	 MOV [R0], R1 			    ; para puder escreve lo no proximo ciclo
	 MOV R2, 14
	 CMP R1, R2
	 JZ call_desliga_missil	    ; quando for zero, chama a rotina que "desliga" o missil
	 JMP printa
  printa:
	 MOV R1, estadoINIT
	 MOV R0, 2
	 MOVB [R1], R0
	 MOV R0, TAB_MISSIL 		; Escreve o bit na linha atual
	 CALL escreveTab
	 JMP sai_missil
	 
  call_desliga_missil:
	 CALL desliga_missil
	 JMP sai_missil
	 
  sai_missil:
	 MOV R0, checkinterrupt1
	 MOV R1, 0
	 MOVB [R0], R1
	 POP R4
	 POP R3
	 POP R2
	 POP R1
	 POP R0
	 RET

;|----------------------------|
;| ROTINAS AUX                |
;|----------------------------|
;|--------------------------------------------------------------------------------|
;|	ROTINA: desliga_missil														  | 
;|	DESCRICAO: apaga a ultima posicao do missil, mete a 0 o bit de estado  		  |
;|  TURN_ON_MISSIL para nao criar outro missil, volta a meter as tabelas 		  |
;|  TAB_MISSIL0 e TAB_MISSIL1 nos seus valores originais.						  |
;|  INPUT- None							 										  |
;|  OUTPUT- Missil desligado e tabelas reiniciadas.		    					  |
;|--------------------------------------------------------------------------------|	 
desliga_missil:
	 PUSH R0
	 PUSH R1
	 PUSH R3
	 PUSH R4
	 PUSH R8
	 PUSH R9
	 
	 MOV R0, checkinterrupt1
	 MOV R1, 0
	 MOVB [R0], R1
	 
	 MOV R3, TURN_ON_MISSIL		; mete o bit de estado a 0 para nao permitir a execucao 
	 MOV R4, 0					; do missil no proximo ciclo
	 MOVB [R3], R4
	 
	 MOV R1, estadoINIT
	 MOV R0, 2
	 MOVB [R1], R0
	 MOV R0, TAB_MISSIL		    ; apaga a ultima posicao do missil
	 CALL apagaObjeto
	 
	 MOV R0, LINHA_MISSIL
	 MOV R1, 27					; Reincia a linha da tabela TAB_MISSIL a 26
	 MOV [R0], R1
	 
	 MOV R0, COLUNA_MISSIL
	 MOV R1, 15					; Reincia a coluna da tabela TAB_MISSIL a 26
	 MOV [R0], R1
	 POP R9
	 POP R8
	 POP R4
	 POP R3
	 POP R1
	 POP R0
	 RET

	 
desliga_ast:
		PUSH R0
		PUSH R1
		PUSH R2
		PUSH R3
		MOV R0, asteroides
		MOV R3, nAsteroides
	 ciclo_desliga:
		MOV R1, [R0]
		MOV R2, 0
		MOV [R1], R2
		
		ADD R0, 2
		SUB R3, 1
		JZ sai_desligaMissil
		JMP ciclo_desliga
		
	 sai_desligaMissil:
		MOV R0, checkInterrupt
		MOV R1, 0
		MOVB [R0], R1
		POP R3
		POP R2
		POP R1
		POP R0
		RET	 

;|--------------------------------------------------------------------------------|
;|	ROTINA: tecla_inutil														  | 
;|	DESCRICAO: 	corresponde a todas as teclas sem funcionalidade no jogo e que,	  |
;|  por isso, simplesmente retornam a nave com o seu volante neutro.			  |
;|  INPUT- None							 										  |
;|  OUTPUT- Nave com volante neutro																  |
;|--------------------------------------------------------------------------------|	 	 
tecla_inutil:
	 RET
	 
nao_ha_tecla:
	 PUSH R0
	 PUSH R1
	 PUSH R4
	 MOV R4,0
	 CALL mexeAst
	 CALL mexeMissil
	 MOV R1, estadoINIT
	 MOV R0, 0
	 MOVB [R1], R0
	 MOV R0, VOLANTENEUTRO
	 CALL escreveTab
	 POP R4
	 POP R1
	 POP R0
	 RET
;|--------------------------------------------------------------------------------|
;|	ROTINA: tecla_0																  | 
;|	DESCRICAO: 	Chama a rotina que vira o volante a esquerda					  |
;|  INPUT- None							 										  |
;|  OUTPUT- Volante a esquerda													  |
;|--------------------------------------------------------------------------------|	 
tecla_0:
	 PUSH R0
	 PUSH R1
	 PUSH R4
	 MOV R4,1
	 CALL mexeAst
	 CALL mexeMissil
	 MOV R1, estadoINIT
	 MOV R0, 0
	 MOVB [R1], R0
	 MOV  R0, VOLANTEESQ
	 CALL escreveTab
	 POP R4
	 POP R1
	 POP R0
	 RET
	 
;|--------------------------------------------------------------------------------|
;|	ROTINA: tecla_1																  | 
;|	DESCRICAO: 	nesta rotina, compara-se se se esta na pausa. Se sim, nao é 	  |
;|  possível "ligar" o missil. Senao, mete a 1 o bit de estado TURN_ON_MISSIL 	  |
;|  permitindo a execucao do missil.											  |	
;|  INPUT- None							 										  |
;|  OUTPUT- Bit de estado TURN_ON_MISSIL a 1 se nao estiver na pausa			  |
;|--------------------------------------------------------------------------------|	 
tecla_1:
	 PUSH R3
	 PUSH R4
	 PUSH R6
	 PUSH R7
	 
	 MOV R7, ESTADO_PAUSA_INICIO
	 MOV R6,[R7]
	 CMP R6, 1						; Se se estiver na pausa, nao se pode "ligar" o missil
	 JZ sai_tecla_1
	 
	 MOV R4, TURN_ON_MISSIL
	 MOV R3, 1						; para permitir a execucao do missil
	 MOVB [R4],R3
	 
  sai_tecla_1:
     POP R7
	 POP R6
	 POP R4
	 POP R3
	 RET
;|--------------------------------------------------------------------------------|
;|	ROTINA: tecla_3																  | 
;|	DESCRICAO: 	Chama a rotina que vira o volante a direita						  |
;|  INPUT- None							 										  |
;|  OUTPUT- Volante a direita													  |
;|--------------------------------------------------------------------------------|	 
tecla_3:
	 PUSH R0
	 PUSH R1
	 PUSH R4
	 MOV R4,-1
	 CALL mexeAst
	 CALL mexeMissil
	 MOV R1, estadoINIT
	 MOV R0, 0
	 MOVB [R1], R0
	 MOV R0, VOLANTEDIR
	 CALL escreveTab
	 POP R4
	 POP R1
	 POP R0
	 RET
	 
;|--------------------------------------------------------------------------------|
;|	ROTINA: tecla_C																  | 
;|	DESCRICAO: 	nesta rotina, mete se o bit de estado ESTADO_INICIALIZACAO a zero |
;|  para nao permitir a entrada no jogo ate ser premida outra vez a tecla C. Mete |
;|  a zero o bit de estado que coordena a pausa. "desliga" o missil para nao      |	
;|  voltar a aparecer ao voltar a entrar no jogo e imprime o ecra inicial.		  |
;|  INPUT- None							 										  |
;|  OUTPUT- Ecra inicial														  |
;|--------------------------------------------------------------------------------|
tecla_C:
	 PUSH R7
	 PUSH R8
	 PUSH R9
	 MOV R9, ESTADO_INICIALIZACAO
	 MOV R8, 0							;bit de estado ESTADO_INICIALIZACAO a zero 
	 MOVB [R9], R8					    ;para nao permitir a entrada no jogo ate ser premida outra vez a tecla C
	 MOV R7, ESTADO_PAUSA_INICIO
	 MOV R8, 0							;Passa a zero o bit de estado que coordena a pausa		
	 MOV [R7], R8
	 
	 CALL desliga_ast
	 CALL desliga_missil				;para nao voltar a aparecer ao voltar a entrar no jogo		
	 MOV R7, ECRA_I
	 CALL print_ecra_inicial			;imprime o ecra inicial
	 
	 POP R9
	 POP R8
	 POP R7
	 RET
	 
;|--------------------------------------------------------------------------------|
;|	ROTINA: tecla_D																  | 
;|	DESCRICAO: 	nesta rotina, compara-se se estamos na pausa(bit de estado a 1).  |
;|  Se sim, mete-se o bit de estado a 0 para permitir sair da pausa. Senao,       |
;|  mete-se o bit de estado a 1 para permitir pausar o jogo.                      |	
;|  INPUT- None							 										  |
;|  OUTPUT- Pausa/sair da pausa												      |
;|--------------------------------------------------------------------------------|
tecla_D:
	 PUSH R6
	 PUSH R7
	 PUSH R8
	 
	 MOV R7, ESTADO_PAUSA_INICIO
	 MOV R6,[R7]
	 CMP R6, 1							; compara se se esta na pausa
	 JZ mete_bit_a_0					; se sim, tira-se da pausa
	 MOV R8, 1							; senao mete se o bit de estado a 1 para entrar da pausa
	 MOV [R7], R8
	 JMP sai_tecla_D
  mete_bit_a_0:
	 MOV R8, 0							; mete se o bit de estado a 0 para sair da pausa
	 MOV [R7], R8
	 JMP sai_tecla_D
  sai_tecla_D: 
	 POP R8
	 POP R7
	 POP R6
	 RET

;|------------------------------------------------------------|
;| ROTINA: addScore                                           |
;| DESCRICAO: Atualiza o score por +=3 e imprime nos displays |
;| INPUT: R1(score atual),R2(tabela do score na memoria       |
;| OUTPUT:                                                    |
;|------------------------------------------------------------|
addScore:
	PUSH R1
	PUSH R2
	PUSH R4
	PUSH R6
	PUSH R7
	PUSH R8
	PUSH R9
	
	ADD R7, 3
	MOV [R2], R7
	MOV R4, DISPLAYS
	MOV R9, R7
	MOV R6, R7
	MOV R10, 10
	MOD R9, R10        ; = digito do low
	DIV R6, R10        ; = digito high
    SHL  R6, 4         ; coloca linha no nibble high
    OR   R6, R9        ; junta coluna (nibble low)
    MOVB [R4], R6      ; escreve linha e coluna nos displays
	JMP feito
feito:
	 POP R9
	 POP R8
	 POP R7
	 POP R6
	 POP R4
	 POP R2
	 POP R1
	 RET

;|--------------------------------------------------------------------------|
;| ROTINA: mexeAst                                                          |
;| DESCRICAO: Mete no espaço de memoria de um asteroide a direção do volante|
;| INPUT : R4(direção do volante), R1(endereço do asteroide                 |
;| OUTPUT : None                                                            |
;|--------------------------------------------------------------------------|	
mexeAst:
	 PUSH R1
	 PUSH R2
	 PUSH R3
	 PUSH R4
	 MOV R2,asteroides
	 MOV R3,nAsteroides
	cicloMexeAst:
	 MOV R1,[R2]
	 ADD R1,6 
	 ADD R1,6
	 MOV [R1],R4
	 ADD R2,2
	 SUB R3,1
	 JZ saiMexeAst
	 JMP cicloMexeAst
	saiMexeAst:
	 POP R4
	 POP R3
	 POP R2
	 POP R1
	 RET

mexeMissil:
	 PUSH R1
	 MOV R1,DIRECAO_MISSIL
	 MOV [R1],R4
	 POP R1
	 RET
	 
;|---------------------------------------------------------------------------------|
;|	ROTINA: espera_inicio													       | 
;|	DESCRICAO: 	nesta rotina, compara-se se estamos estamos a premir a tecla C. Se |
;|  sim, mete o bit de estado ESTADO_INICIALIZACAO a 1 para permitir entrar no     |
;|  jogo, limpa-se o ecra,desenha se a nave com volante neutro, reinicia-se os     |	
;|  displays e contador de pontos. Se nao, sai da funcao com o bit de estado a 0.  |
;|  INPUT-  None						 										   |
;|  OUTPUT- None/ nave com volante neutro 										   |	;|---------------------------------------------------------------------------------|	 
espera_inicio:
	 PUSH R0
	 PUSH R1
	 PUSH R2
	 PUSH R4
	 PUSH R5
	 PUSH R6
	 PUSH R8
	 PUSH R9
	 MOV  R6, TECLA_PREMIDA             ; Vai buscar o valor da tecla premida a memoria
	 MOV  R5, [R6]                                                                                        
	 MOV R9, 12  					    ; Compara-se se estamos a premir a tecla C
	 CMP R5, R9
	 JNZ retorna				      	; Senao, sai da rotina sem fazer nada
	 
	 MOV R9, ESTADO_INICIALIZACAO
	 MOVB R9, [R9]
	 CMP R9, 2							; estado game_over/ganda_jogador
	 JZ ecra_inicial
	 JMP entra_jogo

ecra_inicial:
	 MOV R9, ESTADO_INICIALIZACAO
	 MOV R8, 0							;bit de estado ESTADO_INICIALIZACAO a zero 
	 MOVB [R9], R8					    ;para nao permitir a entrada no jogo ate ser premida outra vez a tecla C
	 MOV R7, ESTADO_PAUSA_INICIO
	 MOV R8, 0							;Passa a zero o bit de estado que coordena a pausa		
	 MOV [R7], R8
	 
	 CALL desliga_ast
	 CALL desliga_missil				;para nao voltar a aparecer ao voltar a entrar no jogo		
	 MOV R7, ECRA_I
	 CALL print_ecra_inicial			;imprime o ecra inicial
	 JMP retorna
	 
entra_jogo:

   	 CALL limpaEcra					    ; rotina que limpa o ecra completo
	 MOV R1, estadoINIT
	 MOV R0, 0
	 MOVB [R1], R0
	 MOV R0, NAVE					    ; imprime a nave
	 CALL escreveTab
	 MOV R1, estadoINIT
	 MOV R0, 0
	 MOVB [R1], R0
	 MOV R0, VOLANTENEUTRO			    ; imprime o volante neutro
	 CALL escreveTab
	 
	 MOV  R4, DISPLAYS                  ; endereço do periférico dos displays
	 MOV R1, 0
     MOVB [R4], R1                      ; inicia-se o valor a zeros dos displays
	 MOV R2, SCORE
	 MOV R1, 0			                ; inicia-se o valor a zeros do contador
	 MOV [R2], R1
	 
	 MOV R9, ESTADO_INICIALIZACAO	    ; Se sim, mete se o bit de estado a 1 para permitir 
	 MOV R8, 1						    ; a execucao do jogo.
	 MOVB [R9], R8
	 JMP retorna
  retorna:
	 POP R9
	 POP R8
	 POP R6
	 POP R5
	 POP R4
	 POP R2
	 POP R1
	 POP R0
	 RET	

;|--------------------------------------------------------------------------------|
;|	ROTINA: print_ecra_inicial	    											  |
;| 	DESCRICAO: nesta rotina, é imprimido os pixeis do ecra inicial				  |   						                    
;|	INPUT:	R7- valor da tabela com os bytes a escrever							  |	 
;|	OUTPUT:	Ecra inicial no PixelScreen						     				  |
;|--------------------------------------------------------------------------------|
;|--------------------------------------------------------------------------------|
;|	ROTINA: escreve_ecra 														  | 
;|	DESCRICAO: Escreve todos os bytes do ecra, negando todos os bits do byte      |
;|  sempre que se muda de linha.												  |
;|  INPUT- R7- valor da tabela com os bytes a escrever							  |
;|  OUTPUT- Ecra inicial no PixelScreen											  |
;|--------------------------------------------------------------------------------|	 
print_ecra_inicial:
	 PUSH  R2
     PUSH  R3
	 
     MOV   R2, 0              ; usado para indicar qual o byte dentro do ecrã
     MOV   R3, BYTES          ; tamanho do ecrã em bytes
	 JMP  escreve
  proximo_byte:
     ADD  R7, 1
  escreve:
     CALL  escreve_byte       ; escreve o byte dado por R7 no ecrã,
                              ; no endereço indicado por R2
     ADD   R2, 1              ; mais um byte escrito
     CMP   R2, R3             ; já chegámos ao fim?
     JLT   proximo_byte
    
     POP   R3
     POP   R2
	 RET

;|--------------------------------------------------------------------------------|
;|	ROTINA: escreve_byte 														  | 
;|	DESCRICAO: Escreve um byte no ecra										      |
;|  INPUT- R7- valor da tabela com os bytes a escrever							  |
;|  	   R2- Número do byte a escrever (entre 0 e BYTES_ECRA - 1)				  |
;|  OUTPUT- Byte escrito no ecra												  |
;|--------------------------------------------------------------------------------|	 
escreve_byte:
     PUSH  R0
	 PUSH  R3
     MOV   R0, ECRA           ; endereço do primeiro byte do ecrã
     ADD   R0, R2             ; calcula endereço onde escrever o byte
	 MOVB   R3, [R7]
     MOVB  [R0], R3           ; escreve o byte no ecrã
	 POP   R3
     POP   R0
     RET

;|---------------------------------------------------------|
;| ROTINA : apagaAsteroide                                 |
;| DESCRICAO : Apaga um asteroide dado o endereço do mesmo |
;| INPUT : R1 endereço do asteroide                        |
;|---------------------------------------------------------|
apagaAsteroide:
	PUSH R0
	PUSH R1
	PUSH R2
	MOV R0,[R1+2]
	CALL apagaObjeto
	saiApagaAsteroide:
		POP R2
		POP R1
		POP R0
		RET

;|--------------------------------------------------------|
;| ROTINA : desenhaAsteroide                              |
;| DESCRIÇÃO : desenha um asteroide dado o seu endereço   |
;| INPUT :R1                                              |
;| OUTPUT : NONE                                          |
;|--------------------------------------------------------|
desenhaAsteroide:
	PUSH R0
	PUSH R1
	PUSH R2
	MOV R2,[R1]
	CMP R2,0
	JZ saiDesenhaAsteroide
	MOV R0,[R1+2]
	CALL escreveTab
	saiDesenhaAsteroide:
		POP R2
		POP R1
		POP R0
		RET
		
;|--------------------------------------------------------------------------------|
;| ROTINA : addColunaLinha                                                        |
;| DESCRICAO : Adiciona +1 linha e adiciona a direção do asteroide e a direção do |
;|			   volante ao valor da coluna aos valores de um asteroide             |
;| INPUT : R1(endereço do asteroide)                                              |
;|--------------------------------------------------------------------------------|	
addColunaLinha:
	PUSH R1
	PUSH R2
	PUSH R3
	PUSH R4
	PUSH R5
	PUSH R7
	MOV R2,[R1+4]							; Coluna do asteroide atual
	MOV R5,10								; Direção do asteroide
	MOV R3,[R1+R5]
	MOV R5,12
	MOV R4,[R1+R5]
	ADD R2,R3
	ADD R2,R4
	CALL resetAstColunaD
	CALL resetAstColunaE
	MOV [R1+4],R2							; Update com a nova coluna
	MOV R2,[R1+6]							; Linha do asteroide atual
	ADD R2,1
	MOV [R1+6],R2
	JMP saiAddColunaLinha
	
	resetAstColunaD:
		PUSH R0
		PUSH R1
		MOV  R0,FimAsteroidesD
		CMP R2,R0
		JLT saiResetAstColunaD
		MOV R0,0
		MOV [R1],R0
		saiResetAstColunaD:
			POP R1
			POP R0
			RET
			
	resetAstColunaE:
		PUSH R0
		PUSH R1
		MOV  R0,FimAsteroidesE
		CMP R2,R0
		JGT saiResetAstColunaE
		MOV R0,0
		MOV [R1],R0
		saiResetAstColunaE:
			POP R1
			POP R0
			RET
			
	saiAddColunaLinha:
		POP R7
		POP R5
		POP R4
		POP R3
		POP R2
		POP R1
		RET

;|------------------------------------------------|
;| ROTINA: updateTamanho                          |
;| DESCRIÇÃO:                                     |
;|------------------------------------------------|
updateTamanho:
	PUSH R1
	PUSH R2
	PUSH R4
	PUSH R5
	PUSH R7
	MOV R2,[R1+6]								; Linha atual
	CALL tamanhoAst
	MOV R5, 8
	MOV R4,[R1+R5]								; Tamanho de asteroide
	ADD R4,R7
	MOV R4,[R4]
	MOV [R1+2],R4								; Substituir pelo novo endereço de acordo com o tamanho
	
	saiUpdateTamanho:
		POP R7
		POP R5
		POP R4
		POP R2
		POP R1
		RET

;|-----------------------------------------------------------------------------|
;| ROTINA: tamanhoAst                                                          |
;| DESCRIÇÃO : Recebe uma linha e retorna um valor correspondente ao tamanhoAst|
;| INPUT: R2(linha)                                                            |
;| OUTPUT; R7(tamanhoAst)                                                      |
;|-----------------------------------------------------------------------------|
tamanhoAst:
	 PUSH R1
	 PUSH R3
	 PUSH R2
	 MOV R3,12
	 MOV R7,8
	 CMP R2,R3
	 JGE return
	 MOV R3,3
	 DIV R2,R3
	 MOV R7,R2
	 SHL R7,1
return:
	 POP R2
	 POP R3
	 POP R1
	 RET

;|--------------------------------|
;|  colisãoAstMissil              |
;|--------------------------------|
colisaoAstMissil:
	 PUSH R0
	 PUSH R1
	 PUSH R2
	 PUSH R3
	 
	 MOV R2, TURN_ON_MISSIL
	 MOVB R3, [R2]
	 CMP R3, 1
	 JNZ saiColisaoAstMissil
	 MOV R2,[R1+6]
	 ADD R2,4
	 MOV R3,LINHA_MISSIL
	 MOV R3,[R3]
	 CMP R2,R3
	 JLT saiColisaoAstMissil
	 MOV R3,COLUNA_MISSIL
	 MOV R3,[R3]
	 MOV R2,[R1+4]
	 CMP R3,R2
	 JLT saiColisaoAstMissil
	 ADD R2,4
	 CMP R3,R2
	 JGT saiColisaoAstMissil
	 JMP colisaoMissil
	 
	colisaoMissil:
		CALL desliga_missil
		CALL apagaAsteroide
		MOV R0,AsteroideExpl
		MOV [R1+2],R0
		CALL desenhaAsteroide
		MOV R0,2
		MOV [R1],R0
		
	saiColisaoAstMissil:
		 POP R3
		 POP R2
		 POP R1
		 POP R0
		 RET
		 
;|--------------------------------------------------------------------------------|
;|	ROTINA: ColisaoAstNave												      |
;| 	DESCRICAO: Nesta rotina, verifica-se se as colisoes com a nave asteroide a    |
;|	asteroide. Se colidir, verifica o tipo de asteroide(bom ou mau) e aumenta o   |
;|	score/ecra ganda jogador ou imprime ecra game over. 						  |
;|	INPUT:	R1- endereço de cada asteroide										  |	 
;|	OUTPUT:	respetivas açoes.						     						  |
;|--------------------------------------------------------------------------------|
ColisaoAstNave:
	 PUSH R1
	 PUSH R2
	 PUSH R3
	 PUSH R4
	 PUSH R5
	 PUSH R6
	 PUSH R7
	 
	 MOV R2, 22								; linha inicio colisoes
	 MOV R0, [R1+6]
	 CMP R0, R2
	 JLT sai_ColisoesAstNave
	 
	 colisaoNave:
	 MOV R7, 8
	 MOV R3, [R1+R7]
	 MOV R2, AsteroidesMaus
	 CMP R3, R2								; compara o tipo de asteroide
	 JZ asteroide_mau		
	 JMP asteroide_bom
	
	asteroide_bom:
	 MOV R2, 0
	 MOV [R1], R2
	 CALL apagaAsteroide
	 
	 MOV R2, SCORE
	 MOV R7, [R2]
	 MOV R3, 99        						; valor maximo do display
	 CMP R7, R3         					; verfica se ja la chegou
	 JZ ecra_ganda_jogador					; Se sim, acaba o jogo e imprime:"ganda jogador"
	 CALL addScore							; Senao apenas adiciona 3 ao score
	 JMP sai_ColisoesAstNave				; sai da funcao

	asteroide_mau:
	 CALL desliga_ast
	 MOV R7, ECRA_GAME_OVER
	 CALL print_ecra_inicial				; impressao do ecra
	 MOV R4, ESTADO_INICIALIZACAO	
	 MOV R3, 2								; mete o estado inicializacao a 2 para 
	 MOVB [R4], R3							; permitir carregar na tecla C para imprimir o ecra inicial
	 JMP sai_ColisoesAstNave
	 
	ecra_ganda_jogador:
	 MOV R7, ECRA_GANDA_JOGADOR				; impressao do ecra
	 CALL print_ecra_inicial
	 MOV R4, ESTADO_INICIALIZACAO	
	 MOV R3, 2								; mete o estado inicializacao a 2 para 
	 MOVB [R4], R3							; permitir carregar na tecla C para imprimir o ecra inicial
	 JMP sai_ColisoesAstNave

	sai_ColisoesAstNave: 
	 POP R7
	 POP R6
	 POP R5
	 POP R4
	 POP R3
	 POP R2
	 POP R1
	 RET
	 
;|------------------------------------------------------------------------------------|
;| ROTINA: escreveTab                                                                 |
;| DESCRIÇÃO: rotina que escreve uma dada tabela                                      |
;| input -   tabela a escrever(R0) e R1 em caso de ser um asteroide                   |
;| output -  print(dada tabela)                    	                                  |
;|------------------------------------------------------------------------------------|
escreveTab:
	 PUSH R0
	 PUSH R1
	 PUSH R2
	 PUSH R4
	 PUSH R5
	 PUSH R6
	 PUSH R7
	 PUSH R8
	 PUSH R9
	 PUSH R10
	 PUSH R11
	 MOV  R10, estadoINIT
	 MOVB R11, [R10]
	 CMP  R11,0
	 JZ  estado0Tab
	 CMP  R11, 1
	 JZ  estado1Tab
	 JMP estado2Tab
	 
	 estado0Tab:
		 MOVB R6, [R0]						; Numero de colunas da tabela
		 ADD R0,1
		 MOV R11,1
		 MOVB [R10],R11
		 MOVB R11, [R0]
		 ADD R0,1							; Altura
		 MOVB R1, [R0]						; Linha inicial
		 ADD R0,1
		 MOVB R2, [R0]						; Coluna inicial
		 ADD R0,1
		 MOV R7,0							; Coluna inicial da tabela
		 MOV R8,R2							; Backup da coluna
		 MOV R10,1							; Contador de linhas
		 JMP cicloEscreveTab
		 
	 estado1Tab:
		MOV R6,[R0]							; Numero de colunas da tabela
		ADD R0,2
		MOV R11,[R0]					    ; Altura da tabela
		ADD R0,2
		MOV R2,[R1+4]
		MOV R1,[R1+6]						; Linha inicial
		MOV R7,0							; Coluna inicial da tabela
		MOV R8,R2							; Backup da coluna
		MOV R10,1							; Contador de linhas
		JMP cicloEscreveTab
		
	  estado2Tab:
		 MOV R6, [R0]						; Numero de colunas da tabela
		 ADD R0,2
		 MOV R11,[R0]
		 ADD R0,2
		 MOV R2, [R0]						; Coluna inicial
		 MOV  R2,[R2]
		 ADD R0,2
		 MOV R1, [R0]						; Endereço da linha inicial a escrever
		 MOV R1,[R1]
		 ADD R0,2
		 MOV R7,0							; Coluna inicial da tabela
		 MOV R8,R2							; Backup da coluna
		 MOV R10,1							; Contador de linhas
		 MOV R9, estadoINIT
		 MOVB [R9], R10
		 
	cicloEscreveTab:
		 MOV R5, LimiteDireita
		 CMP R2,R5
		 JGE proximoBIT						; Verifica se a coluna que vamos escrever é maior que 31
		 MOV R5, LimiteEsquerda
		 CMP R2,R5
		 JLT proximoBIT						; Verifica se a coluna que vamos escrever é menor que 0

		 MOVB R4,[R0]						; Lê o pixel a colocar da tabela
		 CALL printPixel

	proximoBIT:
		 ADD R2,1							; adiciona 1 à coluna do ecrã
		 ADD R7,1							; adiciona 1 à coluna da tabela
		 ADD R0,1							; adiciona 1 ao endereço da tabela para ler o byte a seguir
		 CMP R7,R6							; compara se chegamos à ultima coluna
		 JNZ cicloEscreveTab				; se não então continua

		 CMP R10,R11						; verificação da chegada à ultima linha
		 JZ acabouTab

	mudaLinha:
		 MOV R2,R8							; reset a coluna do ecrã à coluna inicial
	 	 MOV R7,0							; reset a coluna da tabela a 0
	 	 ADD R1,1							; adiciona 1 à linha da tabela
		 ADD R10, 1
		 JMP cicloEscreveTab

acabouTab:
	 POP R11
	 POP R10
	 POP R9
	 POP R8
	 POP R7
	 POP R6
	 POP R5
	 POP R4
	 POP R2
	 POP R1
	 POP R0
	 RET

;|---------------------------------------------------------------------|
;|  ROTINA :  printPixel                                               |
;|  rotina que escreve um pixel dado um estado e a sua linha e coluna  |
;|  input- R1 - linha                                                  |
;|         R2 - coluna                                                 |
;|         R4 - estado do bit (o ou 1)                                 |
;|  output- print(pixel em questão)                                    |
;|---------------------------------------------------------------------|
printPixel:
	 PUSH R1
	 PUSH R2
	 PUSH R3
	 PUSH R5
	 PUSH R6
	 MOV  R3,8
	 SHL  R1,2								; *4  a linha
	 PUSH R2
	 SHR  R2,3								; //8 da coluna
	 ADD  R1,R2								; Byte no ecrã onde está o pixel a pintar
	 POP  R2
	 MOD  R2, R3							; Peso do bit dentro do byte a pintar
	 MOV  R3, mascara
	 ADD  R3, R2							; Descobrir a mascara do pixel a pintar
	 MOVB  R3,[R3]							; Bit a colocar no ecrã
	 CMP  R4,0								; Se R0 é 0 então salta para pixel0
	 JZ   pixel0
	 JMP  pixel1							; Se R0 é 1 então salta para pixel1

pixel0:
	 NOT  R3
	 MOV  R5,ECRA
	 ADD  R5, R1							; Calculo do endereço do byte
	 MOVB  R6, [R5]							; O que está no byte
	 AND   R6, R3							; Altera o que esta no peso R2 no byte com o 0
	 MOVB [R5],R6							; Escreve no endereço R5 o o r6 alterado
	 JMP saiPrintPixel

pixel1:
	 MOV  R5,ECRA
	 ADD  R5, R1							; Calculo do endereço do byte
	 MOVB  R6, [R5]							; O que está no byte
	 OR   R6, R3							; Altera o que esta no peso R2 no byte com o 1
	 MOVB [R5],R6							; O que estava + o bit que queríamos escrever

saiPrintPixel:
	 POP  R6
	 POP  R5
	 POP  R3
	 POP  R2
	 POP  R1
	 RET

;|--------------------------------------------|
;|  ROTINA :  limpaEcra                       |
;|  DESCRIÇÃO : limpa tudo o que está no ecrã |
;|--------------------------------------------|
limpaEcra:
	 PUSH R1
	 PUSH R2
	 PUSH R3
	 MOV R1, 0H
	 MOV R2, ECRA
	 MOV R3, BYTES

	ciclo2:
		MOVB [R2], R1						; mete 0h no endereço R2
		ADD  R2,1							; adiciona 1 ao endereço a escrever
		SUB  R3,1							; subtrai 1 ou contador
		JZ   ecra0
		JMP  ciclo2

	ecra0:
		POP R3
		POP R2
		POP R1
		RET

;|-------------------------------------------------|
;| ROTINA: apagaObjeto                             |
;| Descrição apaga o objeto de uma certa tabela    |
;|-------------------------------------------------|
apagaObjeto:
	 PUSH R0
	 PUSH R1
	 PUSH R2
	 PUSH R3
	 PUSH R4
	 PUSH R6
	 PUSH R7
	 PUSH R8
	 PUSH R9
	 PUSH R10
	 PUSH R11
	 MOV R9, estadoINIT
	 MOVB R9, [R9]
	 CMP R9, 1
	 JNZ estado2ApagaObjeto
	estado1ApagaObjeto:
	 MOV R6, [R0]							;numero de endereços da tabela
	 ADD R0,2
	 MOV R11,[R0]							; Altura da tabela
	 MOV R2, [R1+4]							;coluna inicial
	 MOV R1, [R1+6]							;linha inicial
	 ADD R0,2
	 MOV R7,0								;coluna inicial da tabela
	 MOV R8,R2								;backup da coluna
	 MOV  R4,0
	 MOV R10,1
	 JMP apagar
	estado2ApagaObjeto:						; Estado para apagar o missil
	 MOV R6, [R0]							;numero de endereços da tabela
	 ADD R0,2
	 MOV R11,[R0]
	 ADD R0,2
	 MOV R2, [R0]							;coluna inicial
	 MOV R2,[R2]
	 ADD R0,2
	 MOV R1, [R0]							;linha inicial
	 MOV R1,[R1]
	 ADD R0,2
	 MOV R7,0								;coluna inicial da tabela
	 MOV R8,R2								;backup da coluna
	 MOV  R4,0
	 MOV R10,1
	 MOV R9, estadoINIT
	 MOVB [R9], R10
	 JMP apagar
apagar:
	 MOV R5, LimiteDireita
	 CMP R2,R5
	 JGE proximoBitApagar					; Verifica se a coluna que vamos escrever é maior que 31
	 MOV R5, LimiteEsquerda
	 CMP R2,R5
	 JLT proximoBitApagar					; Verifica se a coluna que vamos escrever é menor que 0
	 
	 CAll printPixel
proximoBitApagar:
	 ADD R2,1								; adiciona 1 à coluna do ecrã
	 ADD R7,1								; adiciona 1 à coluna da tabela
	 ADD R0,1								; adiciona 1 ao endereço da tabela para ler o byte a seguir
	 CMP R7,R6								; compara se chegamos à ultima coluna
	 JNZ apagar								; se não então continua
	 CMP R10,R11
	 JZ saiApagaObjeto
	 MOV R2,R8								; reset a coluna do ecrã à coluna inicial
	 MOV R7,0								; reset a coluna da tabela a 0
	 ADD R1,1								; adiciona 1 à linha da tabela
	 ADD R10, 1
	 JMP apagar
	saiApagaObjeto:
	 POP R11
	 POP R10
	 POP R9
	 POP R8
	 POP R7
	 POP R6
	 POP R4
	 POP R3
	 POP R2
	 POP R1
	 POP R0
	 RET		

;|-----------------------------------------------------|
;| ROTINA: Add_random                                  |
;| Descrição: adiciona um em cada passagem pelo ciclo  |
;|-----------------------------------------------------|
Add_random:
	PUSH R0
	PUSH R1
	MOV R0, random
	MOV R1, [R0]
	ADD R1, 1								; Adiciona um ao contador random
	MOV [R0], R1							; Mete o novo valor do random na memória
	POP R1
	POP R0
	RET

;|--------------------------------------------------|
;| ROTINA: Reset                                    |
;| Descrição: faz reset ao asteroide                |
;|--------------------------------------------------|
Reset:
	PUSH R0
	PUSH R1
	PUSH R2
	PUSH R3
	PUSH R10
	PUSH R11
	MOV R0,1
	MOV [R1],R0								; Ativação do estado do asteroide
	MOV R0,15
	MOV [R1+4],R0							; Reset da coluna do asteroide
	MOV R0,0
	MOV [R1+6],R0							; Reset da linha do Asteroide
	CALL Random								; Geração da direção e do tipo
	MOV R0,probAST
	ADD R0,R10
	MOV R0,[R0]
	MOV R3, 8
	MOV [R1+R3],R0							; Novo tipo de Asteroide
	MOV R3, 10
	MOV [R1+R3],R11							; Nova direção
	MOV R0,[R0]
	MOV [R1+2],R0
	CALL desenhaAsteroide
	saiReset:
		POP R11
		POP R10
		POP R3
		POP R2
		POP R1
		POP R0
		RET

;|-------------------------------------------------|
;| ROTINA: Random                                  |
;| Descrição: gera um valor pseudo-aleatório       |
;|-------------------------------------------------|
Random:
	PUSH R0
	PUSH R9
	MOV R0, random
	MOV R10, [R0]
	MOV R11, R10
	MOV R9, 9
	MOD R10, R9								; cria um valor aleatório para as probablilidades
	SHL R10,1
	MOV R9, 3
	MOD R11, R9
	SUB R11, 1								; cria um valor aleatório para a direção
	CALL Add_random
	POP R9
	POP R0
	RET
