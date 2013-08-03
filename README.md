Purpose
-------
This Bukkit plugin was written to mimic the CraftBook-like functionality of lift signs, for those who like the feature of lift signs but don't necessarily want everything else that CraftBook comes with. Additionally, a similar plugin existed with a similar goal, however it's source was kept closed by the author which means that:

A) It can't be peer-reviewed for correctness, efficiency or backdoors
B) No guarantee it will be supported into the future if the author disappears.
C) No ability to modify it for your own uses and/or contribute back.

Since I have need of such a plugin and for the above reasons will not run a closed source plugin on my own server, I took up writing this plugin primarily for my own use, which of course I open source for others to use or modify as they see fit.


A few notes about implementation
--------------------------------

Each plugin I write, I try to do things a little better than the last. For LiftSign, that meant incorporating common routines from previous plugins into a small library I could reuse, implementing unit tests and to facilitate unit tests, using IoC (Inversion of Control).

The result is the integration of the Google IoC container (Guice) which is a lightweight IoC container (compared to the bigger name in the IoC space: Spring). Lightweight means smaller download size and quicker startup times. In fact, Guice has no measurable impact on startup times compared to plugins I've written without it.

What it does bring to the table is much cleaner code that is easier to maintain and for which unit tests can be written with ease since dependencies are not hard-wired into the code.

The only real downside is that the downloadable JAR is "fatter" by virtue of including Guice in the plugin, but in the days of high-speed networks if someone is complaining about an extra 700k download, they really need to get a better network. Guice of course has some small memory overhead, probably on the order of 100-200k at runtime, but that's far less than even a single player takes up with the chunks loaded around them on a Minecraft server; so again, a negligible overhead for all the benefits that IoC brings.

Sonar
-----
Sonar was used in the development of this plugin, which applies static code analysis to look for obvious bugs and make sure the code is well formatted. I use the "Sonar way with Findbugs" profile with a few tweaks to the default rules that appeal to my personal coding style, such as not requiring braces {} for all if/else conditionals.

If you wish to contribute and want to use Sonar to validate code you write, you can install Sonar (http://www.sonarqube.org/), install my quality profile (http://goo.gl/COZFCo) into your Sonar installation using 'Restore Profile' and then run "mvn sonar:sonar" on the project to get your own Sonar report. Note sonar allows you to assign per-project quality profiles so if you don't like my settings for your other projects you can assign my profile just for this project.
