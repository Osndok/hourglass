/*
 * Hourglass - a time tracking utility.
 * Copyright (C) 2004 Michael K. Grant <mike@acm.jhu.edu>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * --------------------------------------------------------------------
 *
 * CVS Revision $Revision: 1.2 $
 * Last modified on $Date: 2004/03/06 08:34:54 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.framework;

import java.io.File;
import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.hourglass.HourglassTestCase;

/**
 * Unit tests for {@link net.sourceforge.hourglass.framework.LockManager}.
 *
 * @author Michael K. Grant <mike@acm.jhu.edu>
 */
public class LockManagerTests extends HourglassTestCase {
  
  public LockManagerTests(String name) {
    super(name);
  }
  
  public void setUp() throws IOException {
    _tmpDir = Utilities.createTempDirectory();
    _mgr = LockManager.createTestInstance(_tmpDir);
    assertNotNull(_mgr);
  }
  
  public void tearDown() {
    Utilities.deleteRecursive(_tmpDir);
  }
  
  public static Test suite() {
    TestSuite result = new TestSuite(LockManagerTests.class);
    result.setName("LockManager Tests");
    return result;
  }
  
  public void testLockArchive() throws HourglassException {
    String lockfileName = null;

    // Lock a new archive.
    File f1 = _mgr.lockArchive("archive");
    assertTrue(f1.exists());
    assertTrue(_mgr.isArchiveLocked("archive"));
    
    // Try to lock the same archive again.
    try {
      _mgr.lockArchive("archive");
      fail("Alowed to lock a file that is already locked.");
    }
    catch (HourglassException e) {
      assertTrue(_mgr.isArchiveLocked("archive"));
      assertEquals(ErrorKeys.ERROR_KEY_CANNOT_LOCK_ARCHIVE, e.getKey());
      assertEquals(2, e.getArgs().length);
      assertEquals("archive", e.getArgs()[0]);
      
      // Remember the lock file name for later.
      lockfileName = e.getArgs()[1];
      assertTrue(new File(lockfileName).exists());
      assertTrue(lockfileName.endsWith("archive.lock"));
    }
    
    // Unlock the archive.
    _mgr.unlockArchive("archive");
    assertFalse(new File(lockfileName).exists());
    assertFalse(_mgr.isArchiveLocked("archive"));
    
    // Make sure we can perform multiple unlocks with no exception.
    _mgr.unlockArchive("archive");
    
    // Make sure we can relock the existing archive.
    File f2 = _mgr.lockArchive("archive");
    assertTrue(f1.exists());
    assertTrue(_mgr.isArchiveLocked("archive"));
    assertEquals(f1, f2);

    _mgr.unlockArchive("archive");
    assertFalse(_mgr.isArchiveLocked("archive"));
  }
  
  private LockManager _mgr;
  private File _tmpDir;
}
