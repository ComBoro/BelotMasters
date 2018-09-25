package tests;

import net.comboro.Declaration;
import net.comboro.belotbasics.Card;
import net.comboro.belotbasics.Type;

import static org.junit.jupiter.api.Assertions.*;

class DeclarationTest {

    Declaration declarationConseq1 = new Declaration(Card.fromString("8S", "9S", "10S"));
    Declaration declarationConseq2 = new Declaration(Card.fromString("10C", "JC", "QC"));
    Declaration declarationConseq3 = new Declaration(Card.fromString("QH", "KH", "AH"));
    Declaration declarationConseq4 = new Declaration(Card.fromString("8D", "9D", "10D", "JD"));
    Declaration declarationConseq5 = new Declaration(Card.fromString("8S", "9S", "10S", "JS", "QS"));

    Declaration declarationSqaure1 = new Declaration(Card.fromString("JS", "JH", "JC", "JD"));
    Declaration declarationSqaure2 = new Declaration(Card.fromString("9S", "9H", "9C", "9D"));
    Declaration declarationSqaure3 = new Declaration(Card.fromString("AS", "AH", "AC", "AD"));

    @org.junit.jupiter.api.Test
    void filterStrongest() {
    }

    @org.junit.jupiter.api.Test
    void getHighest() {
        assertEquals(Type.Ten, declarationSqaure1.getHighest());
        assertEquals(Type.Queen, declarationSqaure1.getHighest());
        assertEquals(Type.Ace, declarationSqaure1.getHighest());
        assertEquals(Type.Jack, declarationSqaure1.getHighest());
        assertEquals(Type.Queen, declarationSqaure1.getHighest());
    }

    @org.junit.jupiter.api.Test
    void getType() {
    }

    @org.junit.jupiter.api.Test
    void getPoints() {
    }

    @org.junit.jupiter.api.Test
    void nullify() {
    }

    @org.junit.jupiter.api.Test
    void isFromConsequetive() {
        assertTrue(declarationConseq1.isFromConsequetive());
        assertTrue(declarationConseq2.isFromConsequetive());
        assertTrue(declarationConseq3.isFromConsequetive());
        assertTrue(declarationConseq4.isFromConsequetive());
        assertTrue(declarationConseq5.isFromConsequetive());

        assertFalse(declarationSqaure1.isSquare());
        assertFalse(declarationSqaure2.isSquare());
        assertFalse(declarationSqaure3.isSquare());
    }

    @org.junit.jupiter.api.Test
    void isSquare() {
        assertFalse(declarationConseq1.isSquare());
        assertFalse(declarationConseq2.isSquare());
        assertFalse(declarationConseq3.isSquare());
        assertFalse(declarationConseq4.isSquare());
        assertFalse(declarationConseq5.isSquare());

        assertTrue(declarationSqaure1.isSquare());
        assertTrue(declarationSqaure2.isSquare());
        assertTrue(declarationSqaure3.isSquare());
    }
}