/*  Copyright 2010 Ben Ruijl, Wouter Smeenk

This file is part of project2

project2 is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3, or (at your option)
any later version.

project2 is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with project2; see the file LICENSE.  If not, write to the
Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
02111-1307 USA.

 */
package triggers;

import java.util.ArrayList;
import java.util.List;

public class TriggerManager {
    private final List<Trigger> triggers;

    public TriggerManager() {
	triggers = new ArrayList<Trigger>();
    }

    public void addTrigger(Trigger trigger) {
	triggers.add(trigger);
    }

    public void update() {
	for (Trigger trigger : triggers) {
	    // if the condition is met, execute
	    if (trigger.getCondition().isTrue()) {
		trigger.getResponse().execute();
	    }
	}
    }

}
