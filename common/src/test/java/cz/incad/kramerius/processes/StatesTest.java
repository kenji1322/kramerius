/*
 * Copyright (C) 2013 Pavel Stastny
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package cz.incad.kramerius.processes;

import static cz.incad.kramerius.processes.States.*;

import org.junit.Assert;

import junit.framework.TestCase;

public class StatesTest extends TestCase {

    public void testTransitions() {
        Assert.assertTrue(States.isPossible(States.PLANNED, States.STARTED));

        Assert.assertTrue(States.isPossible(States.STARTED, States.RUNNING));
        Assert.assertTrue(States.isPossible(States.STARTED, States.NOT_RUNNING));

        Assert.assertTrue(States.isPossible(States.RUNNING, States.FINISHED));
        Assert.assertTrue(States.isPossible(States.RUNNING, States.FAILED));
        Assert.assertTrue(States.isPossible(States.RUNNING, States.WARNING));
        Assert.assertTrue(States.isPossible(States.RUNNING, States.KILLED));

        Assert.assertTrue(States.isPossible(States.NOT_RUNNING, States.RUNNING));

        Assert.assertTrue(States.isPossible(States.STARTED, States.NOT_RUNNING));

        Assert.assertFalse(States.isPossible(States.NOT_RUNNING, States.STARTED));
        Assert.assertFalse(States.isPossible(States.RUNNING, States.STARTED));
        Assert.assertFalse(States.isPossible(States.RUNNING, States.NOT_RUNNING));
        Assert.assertFalse(States.isPossible(States.FINISHED, States.NOT_RUNNING));
        Assert.assertFalse(States.isPossible(States.FAILED, States.NOT_RUNNING));
        Assert.assertFalse(States.isPossible(States.KILLED, States.NOT_RUNNING));
        Assert.assertFalse(States.isPossible(States.RUNNING, States.NOT_RUNNING));
    }
}
