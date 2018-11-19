package tests;

import net.comboro.belotserver.Declaration;
import net.comboro.belotserver.belotbasics.Card;
import net.comboro.belotserver.belotbasics.Type;

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
    void getHighest() {
        assertEquals(Type.Ten, declarationConseq1.getHighest());
        assertEquals(Type.Queen, declarationConseq2.getHighest());
        assertEquals(Type.Ace, declarationConseq3.getHighest());
        assertEquals(Type.Jack, declarationConseq4.getHighest());
        assertEquals(Type.Queen, declarationConseq5.getHighest());
    }

    @org.junit.jupiter.api.Test
    void getType() {
        assertEquals(Declaration.TYPE_CONSECUTIVE, declarationConseq1.getType());
        assertEquals(Declaration.TYPE_CONSECUTIVE, declarationConseq2.getType());
        assertEquals(Declaration.TYPE_CONSECUTIVE, declarationConseq3.getType());
        assertEquals(Declaration.TYPE_CONSECUTIVE, declarationConseq4.getType());
        assertEquals(Declaration.TYPE_CONSECUTIVE, declarationConseq5.getType());

        assertEquals(Declaration.TYPE_SQUARE, declarationSqaure1.getType());
        assertEquals(Declaration.TYPE_SQUARE, declarationSqaure2.getType());
        assertEquals(Declaration.TYPE_SQUARE, declarationSqaure3.getType());
    }

    @org.junit.jupiter.api.Test
    void getPoints() {
        assertEquals(20, declarationConseq1.getPoints());
        assertEquals(20, declarationConseq1.getPoints());
        assertEquals(20, declarationConseq3.getPoints());
        assertEquals(50, declarationConseq4.getPoints());
        assertEquals(100, declarationConseq5.getPoints());

        assertEquals(200, declarationSqaure1.getPoints());
        assertEquals(150, declarationSqaure2.getPoints());
        assertEquals(100, declarationSqaure3.getPoints());
    }


    @org.junit.jupiter.api.Test
    void isFromConsecutive() {
        assertTrue(declarationConseq1.isFromConsecutive());
        assertTrue(declarationConseq2.isFromConsecutive());
        assertTrue(declarationConseq3.isFromConsecutive());
        assertTrue(declarationConseq4.isFromConsecutive());
        assertTrue(declarationConseq5.isFromConsecutive());

        assertFalse(declarationSqaure1.isFromConsecutive());
        assertFalse(declarationSqaure2.isFromConsecutive());
        assertFalse(declarationSqaure3.isFromConsecutive());
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