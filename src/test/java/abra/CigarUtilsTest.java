package abra;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CigarUtilsTest {
	
	private StringBuffer readContig;
	
	@BeforeMethod
	public void beforeTest() {
		readContig = new StringBuffer();
	}

	@Test (groups = "unit")
	public void testMatchSubset() {
		String contigCigar = "500M";
		int posRelativeToRef = CigarUtils.subsetCigarString(50, 100, contigCigar, readContig);
		assertEquals(readContig.toString(), "100M");
		assertEquals(posRelativeToRef, 50);
	}
	
	@Test (groups = "unit")
	public void testExactMatch() {
		String contigCigar = "100M";
		int posRelativeToRef = CigarUtils.subsetCigarString(0, 100, contigCigar, readContig);
		assertEquals(readContig.toString(), "100M");
		assertEquals(posRelativeToRef, 0);
	}
	
	@Test (groups = "unit")
	public void testDeletion() {
		String contigCigar = "10M50D490M";
		int posRelativeToRef = CigarUtils.subsetCigarString(5, 100, contigCigar, readContig);
		assertEquals(readContig.toString(), "5M50D95M");
		assertEquals(posRelativeToRef, 5);
	}
	
	@Test (groups = "unit")
	public void testSkipDeletionAtStart() {
		String contigCigar = "10M50D490M";
		int posRelativeToRef = CigarUtils.subsetCigarString(10, 100, contigCigar, readContig);
		assertEquals(readContig.toString(), "100M");
		assertEquals(posRelativeToRef, 60);
	}
	
	@Test (groups = "unit")
	public void testSkipDeletionWithinStart() {
		String contigCigar = "10M50D490M";
		int posRelativeToRef = CigarUtils.subsetCigarString(30, 100, contigCigar, readContig);
		assertEquals(readContig.toString(), "100M");
		assertEquals(posRelativeToRef, 80);
	}

	@Test (groups = "unit")
	public void testInsertion() {
		String contigCigar = "10M50I490M";
		int posRelativeToRef = CigarUtils.subsetCigarString(5, 100, contigCigar, readContig);
		assertEquals(readContig.toString(), "5M50I45M");
		assertEquals(posRelativeToRef, 5);
	}
	
	@Test (groups = "unit")
	public void testInsertionAtStart() {
		String contigCigar = "10M50I490M";
		int posRelativeToRef = CigarUtils.subsetCigarString(10, 100, contigCigar, readContig);
		assertEquals(readContig.toString(), "50I50M");
		assertEquals(posRelativeToRef, 10);
	}
	
	@Test (groups = "unit")
	public void testInsertionPartialOverlapAtStart() {
		String contigCigar = "10M50I490M";
		int posRelativeToRef = CigarUtils.subsetCigarString(30, 100, contigCigar, readContig);
		assertEquals(readContig.toString(), "30I70M");
		assertEquals(posRelativeToRef, 10);
	}
	
	@Test (groups = "unit")
	public void testSkipDeletionAtEnd() {
		String contigCigar = "300M50D200M";
		int posRelativeToRef = CigarUtils.subsetCigarString(200, 100, contigCigar, readContig);
		assertEquals(readContig.toString(), "100M");
		assertEquals(posRelativeToRef, 200);
	}
	
	@Test (groups = "unit")
	public void testSkipInsertionAtEnd() {
		String contigCigar = "300M50I200M";
		int posRelativeToRef = CigarUtils.subsetCigarString(200, 100, contigCigar, readContig);
		assertEquals(readContig.toString(), "100M");
		assertEquals(posRelativeToRef, 200);
	}
	
	@Test (groups = "unit")
	public void testInsertionPartialOverlapAtEnd() {
		String contigCigar = "300M50I200M";
		int posRelativeToRef = CigarUtils.subsetCigarString(201, 100, contigCigar, readContig);
		assertEquals(readContig.toString(), "99M1I");
		assertEquals(posRelativeToRef, 201);
	}
	
	@Test (groups = "unit")
	public void testReadBeyondInsertion() {
		String contigCigar = "100M50I400M";
		int posRelativeToRef = CigarUtils.subsetCigarString(300, 100, contigCigar, readContig);
		assertEquals(readContig.toString(), "100M");
		assertEquals(posRelativeToRef, 250);
	}
	
	@Test (groups = "unit")
	public void testReadBeyondDeletion() {
		String contigCigar = "100M50D400M";
		int posRelativeToRef = CigarUtils.subsetCigarString(300, 100, contigCigar, readContig);
		assertEquals(readContig.toString(), "100M");
		assertEquals(posRelativeToRef, 350);
	}

	@Test (groups = "unit")
	public void testMultipleIndels() {
		// 100M
		// 50D
		// 10I
		// 10M
		// 10I
		// 20D
		// 400M
		String contigCigar = "100M50D10I10M10I20D400M";
		int posRelativeToRef = CigarUtils.subsetCigarString(50, 100, contigCigar, readContig);
		assertEquals(readContig.toString(), "50M50D10I10M10I20D20M");
		assertEquals(posRelativeToRef, 50);
	}

	@Test (groups = "unit")
	public void testBeyondMultipleIndels() {
		// 100M
		// 50D
		// 10I
		// 10M
		// 10I
		// 20D
		// 400M
		String contigCigar = "100M50D10I10M10I20D400M";
		int posRelativeToRef = CigarUtils.subsetCigarString(200, 100, contigCigar, readContig);
		assertEquals(readContig.toString(), "100M");
		assertEquals(posRelativeToRef, 250);
	}
	
	@Test (groups = "unit")
	public void testExtendContig() {
		String cigar = "50M10D50M";
		String newCigar = CigarUtils.extendCigarWithMatches(cigar, 10, 15);
		assertEquals(newCigar, "60M10D65M");
		
		cigar = "100M";
		newCigar = CigarUtils.extendCigarWithMatches(cigar, 10, 15);
		assertEquals(newCigar, "125M");
	}
}