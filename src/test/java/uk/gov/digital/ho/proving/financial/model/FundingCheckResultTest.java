package uk.gov.digital.ho.proving.financial.model;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @Author Home Office Digital
 */
public class FundingCheckResultTest {

    @Test
    public void shouldNotFormatSortCodeWhenInvalidForm_tooShort() throws Exception{

        FundingCheckResult fcr = new FundingCheckResult("11223", null, false, null, null, null);

        assertEquals("11223", fcr.getSortCode());
    }

    @Test
    public void shouldNotFormatSortCodeWhenInvalidForm_tooLong() throws Exception{

        FundingCheckResult fcr = new FundingCheckResult("1122334", null, false, null, null, null);

        assertEquals("1122334", fcr.getSortCode());
    }

    @Test
    public void shouldFormatSortCodeAddingHyphens() throws Exception{

        FundingCheckResult fcr = new FundingCheckResult("112233", null, false, null, null, null);

        assertEquals("11-22-33", fcr.getSortCode());
    }

}
