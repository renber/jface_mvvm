# JFace_MVVM

This Java project contains helper and utility classes for SWT/JFace to accommodate developing standalone Model-View-ViewModel (MVVM) applications (without Eclipse RCP / e4).
JFace databinding is quite powerful. Unfortunately, to leverage the power of a MVVM-based design in a Java application it missing some functionalities. This library aims at resolving these deficits and builds on top of SWT/JFace.

It brings functionality known from C#/WPF MVVM design to Java, such as control templating, commands, auto-updating viewers (you'll never have to manually call refresh() on your Viewers again).

