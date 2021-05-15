;|----------------------------------------------------------------------------------------------------------------------|
;|PROJETO DESENVOLVIDO POR:                                                                                             |
;|Diogo Neutel                                                                                                          |
;|Martim Pimentel                                                                                                       |
;|Beatriz Venceslau nº93734                                                                                             |
;|----------------------------------------------------------------------------------------------------------------------|

; |---------------------------------------------------------------------|
; | Constantes                                                          |
; |---------------------------------------------------------------------|
DISPLAYS   EQU 0A000H  ; endereço dos displays de 7 segmentos (periférico POUT-1)
TEC_LIN    EQU 0C000H  ; endereço das linhas do teclado (periférico POUT-2)
TEC_COL    EQU 0E000H  ; endereço das colunas do teclado (periférico PIN)
LINHA      EQU 8       ; linha a testar (4ª linha, 1000b)
ECRA       EQU 8000H   ; endereço do ecrã (pixelscreen)



PLACE       1000H

nave:      	STRING 5,32,27,0
			STRING 00001111b, 11111111b, 11111111b, 11110000b
			STRING 00010000b, 00000000b, 00000000b, 00001000b
			STRING 00100000b, 00000000b, 00000000b, 00000100b
			STRING 01000000b, 00000000b, 00000000b, 00000010b
			STRING 10000000b, 00000000b, 00000000b, 00000001b
			
volante:    STRING 3, 4, 29, 14
			STRING 00000000b
			STRING 11110000b
			STRING 00000000b

volanteDireita:
			STRING 3, 4, 29, 14
			STRING 10000000b
			STRING 01100000b
			STRING 00010000b
			
volanteEsquerda:
			STRING 3, 4, 29, 14
			STRING 00010000b
			STRING 01100000b
			STRING 10000000b
			
asteroide:  STRING 3, 8, 20, 10
			STRING 00011000b
			STRING 00111100b
			STRING 00011000b
			
mensagem:   STRING 
			

PLACE       1300H

mascara:    STRING  80h 
			STRING  40H
			STRING  20H
			STRING  10H  
			STRING  8H
			STRING  4H
			STRING  2H
			STRING  1H  
			
PLACE       1500H
pilha:      TABLE 100H        ; espaço reservado para a pilha 
                              ; (200H bytes, pois são 100H words)
SP_inicial:                   ; este é o endereço (1200H) com que o SP deve ser 
                              ; inicializado. O 1.º endereço de retorno será 
                              ; armazenado em 11FEH (1200H-2)

PLACE       0
	MOV  SP, SP_inicial
	CALL inicializacao
	CALL teclado
fim:
	JMP fim
	
	
	
	
;|---------------------------------------|
;| ROTINAS                               |
;|---------------------------------------|

;|------------------------------------------|
;| ROTINA:  inicializacao			 	    |
;| DESCRIÇÃO: initializa o dashboard da nave|
;| input - no input                         |
;| output - dashboard inicializado          |
;|------------------------------------------|

inicializacao:                ; inicialização do dashboard e do volante
	 MOV  R0, nave
	 CALL leInfoTab
	 MOV  R0, volante
	 CALL leInfoTab
	 RET
	 
;|---------------------------------------------------|
;| ROTINA: escreveTab                                |
;| DESCRIÇÃO: rotina que escreve uma dada tabela     |
;| input -   tabela a escrever(R0)                   |
;| output -  print(dada tabela)                      |
;|---------------------------------------------------|	

leInfoTab:
	 PUSH R1
	 PUSH R2
	 PUSH R3
	 PUSH R4
	 PUSH R5
	 PUSH R6                  ;  incrementação de 1 para ler as informações sobre a tabela a escrever
	 MOVB  R3,[R0]            ;  altura do objeto
	 ADD   R0,1
	 MOVB  R4,[R0]			  ;  comprimento do objeto 
	 ADD   R0,1
	 MOVB  R1,[R0]    	 	  ;  coordenada x inicial(linha)
	 ADD   R0,1
	 MOVB  R2,[R0]            ;  coordenada y inicial(coluna)
	 ADD   R0, 1
	 MOV   R5, 0              ;  coluna da tabela atual
	 MOV   R6, 0              ;  linha da tabela atual
	 MOV   R7, -1             ;  numero do byte da tabela atual
	 MOV   R8, R2             ;  backup da coluna
	 MOV   R9, R0             ;  backup do endereço da tabela
	 
escreveTab:
     PUSH  R5
	 PUSH  R8
	 MOV   R8,8
	 MOD   R5, R8             ; peso do bit a escrever
	 POP   R8                 
	 CALL  numeroByte 
	 CMP   R6,R3              ; e verifica-se se linha da tabela = altura 
	 JZ    acabouTabela       ; se for então acabouTabela
	 MOV   R0,R9
	 ADD   R0,R7
	 MOVB  R0,[R0]
	 CALL  oQueEscrever
	 POP   R5
	 JMP   callEscrevePixel
	 
acabouTabela:
	 POP R5
	 POP R6
	 POP R5
	 POP R4
	 POP R3
	 POP R2
	 POP R1
	 RET
	 
oQueEscrever:
	 PUSH  R1
	 MOV   R1,mascara           ; coloca no R1 a mascara que permite descobrir o peso do bit 
	 ADD   R1,R5                ; descobre o único bit a 1 (o bit onde estamos)
	 MOVB  R1,[R1]              ;
	 AND   R0,R1                ; aqui está o que tenho que escrever
	 CMP   R0,0                 ; transforma o byte R0, no estado 0 ou 1
	 JZ    bit0
	 
bit1:
	 MOV   R0,1
	 JMP  continuar 
	 
bit0:
	 MOV  R0,0
	 
continuar:
	 POP   R1
	 RET
	 
numeroByte:                      ; descobre o numero do byte atual da tabela
	 CMP   R5, 0                 ; se o peso do bit for zero 
	 JNZ   return                ;       se o peso do bit não for zero volta-se para a rotina original
	 ADD   R7, 1                 ; incrementa o nº do byte em 1
	 
return:
	 RET

callEscrevePixel:
	 CALL printPixel
	 ADD  R2, 1       ;passa para a coluna seguinte
	 ADD  R5, 1       ;incrementa em um o peso do bit
	 CMP  R5, R4      ;compara o peso do bit (posição onde estamos) ao comprimento do objeto
	 JNZ  proximoBit
	 MOV  R5, 0
	 MOV  R2, R8      ; R8 é o backup da coluna
	 ADD  R1, 1       ; R1 é a linha
	 ADD  R6, 1       ; R6 é a linha da tabela atual
	 
proximoBit:
	 JMP  escreveTab
	 
	 
;|--------------------------------------------------------------------|
;|  ROTINA :  printPixel                                              |
;|  rotina que escreve um pixel dado um estado e a sua linha e coluna |
;|  input- R1 - linha                                                 |
;|         R2 - coluna                                                |
;|         R0 - estado do bit (o ou 1)                                |
;|  output- print(pixel em questão)                                   |
;|--------------------------------------------------------------------|

printPixel:
	 PUSH R1
	 PUSH R2
	 PUSH R3
	 PUSH R5
	 PUSH R6
	 MOV  R3,8
	 SHL  R1,2
	 PUSH R2
	 SHR  R2,3
	 ADD  R1,R2            ; byte onde está o pixel a pintar
	 POP  R2
	 MOD  R2, R3           ; peso do bit dentro do byte
	 MOV  R3, mascara
	 ADD  R3, R2
	 MOVB  R3, [R3]        ; bit a colocar no ecrã
	 CMP  R0,0
	 JZ   pixel0
	 JMP  pixel1
	 
pixel0:
	 NOT  R3
	 MOV  R5, ECRA
	 ADD  R5, R1           ; calculo do endereço do byte
	 MOVB  R6,[R5]        ; o que está no byte
	 AND  R6, R3
	 MOVB [R5],R6          ; o que estava + o bit que queriamos escrever
	 POP  R6
	 POP  R5
	 POP  R3
	 POP  R2
	 POP  R1
	 RET
	 
pixel1:
	 MOV  R5, ECRA
	 ADD  R5, R1           ; calculo do endereço do byte
	 MOVB  R6, [R5]        ; o que está no byte
	 OR   R6, R3
	 MOVB [R5],R6          ; o que estava + o bit que queriamos escrever
	 POP  R6
	 POP  R5
	 POP  R3
	 POP  R2
	 POP  R1
	 RET
	 
	 
;|-----------------------------------------------------|
;| ROTINA:  teclado                                    |
;| DESCRIÇÃO: deteta se houver uma tecla               |
;| input -  não há input                               |
;| output - ativa uma função se houver uma certa tecla |
;|-----------------------------------------------------|
teclado:
	PUSH R0
	PUSH R1
	PUSH R2
	PUSH R3
	PUSH R4
	PUSH R5
	PUSH R6
	PUSH R7
	PUSH R8
	PUSH R9
	PUSH R10
	
; inicializações
    MOV  R2, TEC_LIN   ; endereço do periférico das linhas
    MOV  R3, TEC_COL   ; endereço do periférico das colunas
    MOV  R4, DISPLAYS  ; endereço do periférico dos displays
	

; corpo principal do programa
	MOV  R5,0
    MOV  R1,0
    MOVB [R4], R1       ; inicia-se o valor a zeros dos displays
	

ciclo:
    MOV  R1, LINHA 

	
espera_tecla1:          ; neste ciclo espera-se até uma tecla ser premida

    MOVB [R2], R1       ; escrever no periférico de saída (linhas)
    MOVB R0, [R3]       ; ler do periférico de entrada (colunas)
    CMP  R0, 0          ; há tecla premida? 
    JNZ  mudancas      ;  tecla premida, impressao
    SHR  R1, 1          ; mudança de linha
    JZ  ciclo            ; quando R1 for 0, volta a mete-lo a 8
    JMP espera_tecla1
	
	
mudancas:
     MOV  R6, R1        ; R1 vai ser alterado por isso armazena-se o valor da linha em R6
     MOV  R7, -1
     MOV  R8 , -1
	 
mudanca_numLinha:
     SHR  R1, 1
     ADD  R7, 1
     CMP  R1, 0
     JNZ  mudanca_numLinha
	 
mudanca_numColuna:
	 SHR  R0, 1
     ADD  R8, 1
     CMP  R0, 0
     JNZ  mudanca_numColuna
    
     SHL R7, 2          ; SHL por 2 é multiplicar por 4
     ADD R7, R8         ; tecla= 4*linha + coluna
	 
tecla_0:
     CMP R7, 0          ; verifica qual é a tecla
	 JNZ tecla_3
	 
	 MOV R0,volanteEsquerda ; mudança direção esquerda( por codigo)
	 CALL leInfoTab
	 JMP impressao

	
tecla_3:
    CMP R7, 3
	JNZ tecla_4
	
	MOV R0, volanteDireita; mudança direção direita( por codigo)
	CALL leInfoTab
	JMP impressao

tecla_4:                ; botao incremento 3 unidades
    CMP R7, 4
	JNZ espera_tecla1
	MOV R8, 99          ; valor maximo do display
	CMP R10, R8         ; verfica se ja la chegou
	JZ  impressao
	ADD R10, 3          ; Se o utilizador clica nesta tecla e 0<num<96, aumenta o valor do contador em 3 unidades
	JMP impressao	

impressao:
	 MOV R9, R10
	 MOV R5, R10
	 MOV R8, 10
	 MOD R9, R8         ;  = digito do low
	 DIV R5, R8         ; = digito high
     SHL  R5, 4         ; coloca linha no nibble high
     OR   R5, R9        ; junta coluna (nibble low)
     MOVB [R4], R5	    ; escreve linha e coluna nos displays
	 CALL escreve_mensg ; vai chamar a função que escreve a mensagem
	 
	 JMP ha_tecla

    
ha_tecla:              ; neste ciclo espera-se até NENHUMA tecla estar premida

    MOV  R1, R6        ; testar a linha   (R1 tinha sido alterado e vai se buscar o seu valor original a R6)
    MOVB [R2], R1      ; escrever no periférico de saída (linhas)
    MOVB R0, [R3]      ; ler do periférico de entrada (colunas)
    CMP  R0, 0         ; há tecla premida?
    JNZ  ha_tecla      ; se ainda houver uma tecla premida, espera até não haver
	MOV  R0,volante
	CALL leInfoTab
	   ;testar linhas
	JMP  ciclo         ; repete ciclo
	
	
escreve_mensg:
	MOV R0, mensagem
	CALL leInfoTab

	
	 

	
