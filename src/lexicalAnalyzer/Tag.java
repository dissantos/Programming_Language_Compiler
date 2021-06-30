package lexicalAnalyzer;

//Implementação baseada na existente do livro Compiladores – Princípios, técnicas e ferramentas.
//Aho, A. V et al. 2ª edição

public class Tag {
    //Reserved words
    public final static int CLASS = 256;
    public final static int INT = 257;
    public final static int STRING = 258;
    public final static int FLOAT = 259;
    public final static int INIT = 260;
    public final static int STOP = 261;
    public final static int IF = 262;
    public final static int ELSE = 263;
    public final static int DO = 264;
    public final static int WHILE = 265;
    public final static int READ = 266;
    public final static int WRITE = 267;

    //Operators
    public final static int NE = 268; // not equal - !=
    public final static int EQ = 269; // equal - ==
    public final static int LE = 270; // less equal - <=
    public final static int GE = 271; // greater equal - >=
    public final static int OR = 272; // OR Logic - ||
    public final static int AND = 273;// AND Logic - &&

    //OTHERS
    public final static int LITERAL = 274;
    public final static int NUMBER = 275;
    public final static int ID = 276;
}
