# CS4013 Object Oriented Development
## Objectives:
- Apply an object-oriented approach to the design and development of a software application in Java.
- Apply the concepts and techniques introduced in lectures, tutorials and labs to thedesign and development of a small software application.
## Payroll System Requirements:
You are required to build a Payroll system for staff. There are similar salary scales for
hourly paid employees. For example, there may be different scales such as Lecture,
Tutorial, Lab, Demo, Exam Invigilator, Exam Supervisor etc... Each of these scales
would also have several points. Part-time employees must submit a pay claim form by the
second Friday of a month in order to be paid that month. The payroll system should
generate pay slips for all full-time staff and hourly paid staff (with current claims) on the
25th day of each month.

When calculating net pay for a payslip, certain deductions must be made. These include
deductions for Health Insurance, Union Fees, and for the various taxes (e.g. PRSI, USC,
Income Tax).

In October each year, full-time staff are moved to the next point on their salary scale (if
they are not already at the top of that scale. From time to time an employee may be
promoted to the next salary scale within their professional category
(Academic/Administrative/IT/Technical etc..). The point to which a staff member is
promoted to in the new salary scale is dependent on the number of years spent at the top of
the previous scale.

All relevant data associated with the payroll system (such as employee, payroll, payslip
details, including historic payslips) and any other files you deem appropriate for this
application should be stored as csv files.

The application has three user types. An employee can log in to the system, see their
details, and view their most recent or historical payslips. An admin user can log in and
can add a new employee to the system. A human resources user can log in to implement
the promotion functionality for full-time staff. In such cases the staff member should be
asked to confirm the promotional changes being applied. A command line interface (CLI)
should be included to facilitate this interaction between users and the application. The
CLI should be included with the expectation that a Graphical User Interface (GUI) may
be required in the future. In other words, the separation between the text-based user
interface should be well-defined to allow a graphical user interface to be substituted
easily. 
