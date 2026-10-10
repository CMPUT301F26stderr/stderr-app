# Project Problem Description

> **Warning:** This is subject to change!

## Campsite Reservation System Application

You are to design and implement a simple, attractive, and easy-to-use Android application to satisfy the following goals. Your design must be flexible enough to allow developers to extend or migrate it.

## Description

Campsites are vital to Alberta tourism. This province is home to five spectacular national parks and people love to visit them. Campsites are hard to reserve and even harder when you have to book them so far in advance. Furthermore, they are all spread out across different websites and applications.

We want a mobile application where people can make reservations at campsites that are popular and fill up fast. We want to allow people with limitations such as work, disability, etc. to be able to sign up for these events fairly and not have to sit refreshing a webpage until they can get a chance at reserving a spot.

How? Lottery! If I am a busy campsite owner, I will post my campsite or campsites for a block of time and I will let everyone join the waiting list for a period of a week. After the week is up, I will ask the system to choose 5 people to reserve in a first-come first-serve basis. The system will then notify the people. If they say no they do not want to reserve, they are free to do so. I can monitor the progress and then access to the final list of everyone who reserves.

Lottery systems are great because you don't have to be first to get a chance to go, you just have to say you are interested and if you're lucky you will be offered a chance. This gives people who need the time, the time to sign up properly without a time pressure. Accessibility!

## Features

### 1. Pooling System

- Campsite owners can draw from a waiting list of interested entrants to reserve their stay at the campsite.

### 2. Availability Schedule

- A calendar view of availability set by a campsite owner for their campsite.
- Used by entrants to reserve on a first-come first-served basis.

### 3. QR Code Scanning

- Entrants can scan QR promotional code to view details about the campsite and also join the waiting list for an active lottery.

### 4. Firebase Integration

- Utilize Firebase for storing campsite details, reservation lists, and real-time status updates.

### 5. Multi-User Interaction

- Distinguish between entrants, campsite owners, and admin with special roles and privileges granted to each actor.

### 6. Image Upload

- Allow campsite owners to upload promo images.
- Allow entrants to upload profile images.

### 7. Geolocation Support

- Be able to attach a geolocation to the campsite listing. This is the location provided by the campsite owner.
- Entrants can view the campsite location by its geolocation on a map.

## Scenarios

### Scenario 1: Entrant Joining a Lottery

I want to reserve a campsite near Banff so I can go rock climbing. I go to the University of Alberta Van Vliet Complex and see that a poster for a campsite in the Banff National Park area. I scan the QR code and view the campsite listing in the app. I see they have listed availability for the dates I am looking to go and an active lottery for 2 more days. So, I click join waiting list. 2 days later, I was selected by the lottery to book. When I go to book, I see that the campsite has been booked for those dates I wanted. Since the dates I wanted were booked, I click decline on the lottery. Boo.

### Scenario 2: Campsite Owner Creating a Listing

I own a campsite in the Hinton-Grande Cache area. The campsite can accommodate up to 8 people and it includes an outdoor cookhouse and an on-site tennis court. I register my campsite in the app and enter that it is available for the weekends in May 2027. The price is $65 per night. I click publish. Then, I create a campsite lottery listing for the weekends in May. People can also scan the automatically generated QR code to view the listing with details about the campsite and join the waiting list.

### Scenario 3: Entrant Reserving and Receiving a Refund

I want to reserve for a stay in Jasper National Park for 2 nights for me and my friends. I open the app, filter campsites by date and location, and see they have a Jasper campsite listed during those dates. However, the campsite I wanted has no available lottery. I keep browsing and find other campsites that have active lotteries. I join waiting list for a few campsites. I get a notification that I won and a campsite is available to book, and I reserve it. Later, there is a forest fire warning in the campsite area. I look on the app and notice my reservation was rejected by the owner and I got a refund. Phew!

## Actors

- **Entrant:** Someone, on behalf of a group, who declares their interest in reserving a campsite.
- **Campsite Owner:** The person or organization that creates and manages campsite listings.
- **Administrator:** The entity that administers and moderates the application infrastructure.

## Glossary

- **QR Code:** A scannable code, either a barcode, a QR code, or other code scannable by QR code scanning libraries.
- **Waiting List:** A list of entrants who show interest in a specific campsite listing.
- **Sign Up:** An action of registering/confirming for a campsite when successfully selected.
- **Reserve:** An action of confirming a campsite reservation opportunity.
- **Profile:** A screen on the application that holds information about an entrant.
- **Campsite:** A name, location, maximum occupancy, price, rating, optional promotional image, rules and cancellation policy, availability, and campsite owner.

---

# User Stories

User needs are expressed in the form of partial user stories:

**As a (role), I want (goal).**

These descriptions may change to correct omissions and clarify noticed issues. New requirements will be introduced for the final project part.

## 1. Entrant (User)

### 1.1 Campsite Search and Discovery

- **US 01.01.01:** As an entrant, I want to search for campsites.
- **US 01.01.02:** As an entrant, I want to filter campsites by location, date, and number of guests.
- **US 01.01.03:** As an entrant, I want to see a list of campsites based on my filters.
- **US 01.01.04:** As an entrant, I want to see campsite summary information including name, location, maximum occupancy, and rating.
- **US 01.01.05:** As an entrant, I want to see details of a campsite.
- **US 01.01.06:** As an entrant, I want to join an active lottery from the campsite details screen.

### 1.2 Lottery Waiting List

- **US 01.02.01:** As an entrant, I want to join the waiting list for a campsite lottery.
- **US 01.02.02:** As an entrant, I want to leave the waiting list for a campsite lottery.

### 1.3 Lottery Notifications

- **US 01.03.01:** As an entrant, I want to receive a notification when I win the campsite lottery.
- **US 01.03.02:** As an entrant, I want to receive a notification when I lose the campsite lottery.

### 1.4 Lottery Reservation

- **US 01.04.01:** As an entrant, if I win the lottery, I want to see available dates that I can reserve the campsite for.
- **US 01.04.02:** As an entrant, if I win a multiple campsite lottery, I want to choose which campsite to reserve and which dates I want.
- **US 01.04.03:** As an entrant, if I win the lottery, I want to decline a reservation if I changed my mind or if none of the remaining dates work for me.

### 1.5 Reservation Confirmation

- **US 01.05.01:** As an entrant, I want to receive confirmation of my campsite reservation.
- **US 01.05.02:** As an entrant, I want to receive reservation by notification or downloadable CSV.

### 1.6 Reservation History

- **US 01.06.01:** As an entrant, I want to view a list of my active reservations.
- **US 01.06.02:** As an entrant, I want to view a list of my past reservations.

### 1.7 Reservation Cancellation and Profile Updates

- **US 01.07.01:** As an entrant, I want to cancel my campsite reservation.
- **US 01.07.02:** As an entrant, I want to receive confirmation of my campsite cancellation by notification or downloadable CSV.
- **US 01.07.03:** As an entrant, I want to update my personal information, such as name, email, and phone number, on my profile.

### 1.8 Profile Deletion

- **US 01.08.01:** As an entrant, I want to delete my profile.

### 1.9 Device Identification

- **US 01.09.01:** As an entrant, I want to be identified by my device, so that I do not have to use a username and password.

### 1.10 Notification Preferences

- **US 01.10.01:** As an entrant, I want to opt out of receiving notifications.

### 1.11 Policies and Guidelines

- **US 01.11.01:** As an entrant, I want to see the app policy and guidelines.

### 1.12 QR Code Scanning

- **US 01.12.01:** As an entrant, I want to view campsite details by scanning the promotional QR code.

### 1.13 Campsite Ratings

- **US 01.13.01:** As an entrant, I want to give a rating for a campsite.

---

## 2. Campsite Owner

### 2.1 Campsite Registration and Management

- **US 02.01.01:** As a campsite owner, I want to register my campsite with the app.
- **US 02.01.02:** As a campsite owner, I want to provide campsite details including name, location, description (amenities), maximum occupancy, map location, optional image, and tags.
- **US 02.01.03:** As a campsite owner, I want to update details about my campsite.
- **US 02.01.04:** As a campsite owner, I want to view details of my campsite.

### 2.2 QR Code Generation

- **US 02.02.01:** As a campsite owner, I want the system to generate a unique QR code that links to the campsite details.

### 2.3 Campsite List

- **US 02.03.01:** As a campsite owner, I want to view a list of my campsites.

### 2.4 Lottery Creation and Management

- **US 02.04.01:** As a campsite owner, I want to list my campsite in a lottery for a certain period.
- **US 02.04.02:** As a campsite owner, I want be able to list multiple campsites in a lottery for a certain period.
- **US 02.04.03:** As a campsite owner, I want to set a registration period.
- **US 02.04.04:** As a campsite owner, I want to set a booking period.
- **US 02.04.05:** As a campsite owner, I want to set the number of lottery winners.
- **US 02.04.06:** As a campsite owner, I want to update my campsite lottery listing.
- **US 02.04.07:** As a campsite owner, I want to optionally set a capacity for my lottery waitlist.

### 2.5 Automatic Lottery Draw

- **US 02.05.01:** As a campsite owner, I want the system to automatically draw winners after the registration period ends.

### 2.6 Availability Calendar

- **US 02.06.01:** As a campsite owner, I want to view the currently booked dates for my campsite.

### 2.7 Reservation Management

- **US 02.07.01:** As a campsite owner, I want to view a list of active reservations.
- **US 02.07.02:** As a campsite owner, I want to view a list of past reservations.
- **US 02.07.03:** As a campsite owner, I want to view a list of cancelled reservations.

### 2.8 Reservation Rejection

- **US 02.08.01:** As a campsite owner, I want to reject a reservation.

### 2.9 Waitlist Management

- **US 02.09.01:** As a campsite owner, I want to view a list of entrants who have joined the waitlist for my campsite lottery.

### 2.10 Notifications

- **US 02.10.01:** As a campsite owner, I want to send notifications to entrants who have joined the waitlist for my campsite lottery.
- **US 02.10.02:** As a campsite owner, I want to view a list of all notifications I have sent.

### 2.11 Rules and Policies

- **US 02.11.01:** As a campsite owner, I want entrants to confirm that they have read the campsite rules and policies before confirming their reservation.

### 2.12 Messaging

- **US 02.12.01:** As a campsite owner, I want to message a user who is reserving my campsite.

---

## 3. Administrator (Admin)

### 3.1 Campsite Moderation

- **US 03.01.01:** As an admin, I want to remove registered campsites.

### 3.2 Profile Moderation

- **US 03.02.01:** As an admin, I want to remove profiles.

### 3.3 Image Moderation

- **US 03.03.01:** As an admin, I want to remove images.

### 3.4 Campsite Browsing

- **US 03.04.01:** As an admin, I want to browse campsites.

### 3.5 Profile Browsing

- **US 03.05.01:** As an admin, I want to browse profiles.

### 3.6 Image Browsing

- **US 03.06.01:** As an admin, I want to browse uploaded images so I can remove them if necessary.

### 3.7 Campsite Owner Moderation

- **US 03.07.01:** As an admin, I want to remove campsite owners that violate app policy.

### 3.8 Notification Logs

- **US 03.08.01:** As an admin, I want to review logs of all notifications.

### 3.11 Multiple Roles

- **US 03.11.01:** As an admin, I should be able to be a campsite owner and/or entrant with my admin profile.

### 3.12 Message Logs

- **US 03.12.01:** As an admin, I want to review logs of all messages from campsite owners to entrants who reserved their campsite.

---

# WOW Factor

As a bonus **2% to your overall course grade**, project teams are able to complete a "wow factor" that differentiates their project from other teams.

This is an optional component and you can still receive 100% in the course even if you do not complete this.

Some sample ideas of additions to the project are provided below. If you have a different idea than one of the suggestions below then you must approve it with your mentor TA first before working on the feature.

## 1. Reservation Map

- Entrants can view a map of their past and active reservations.

## 2. Match Score

- Campsites can be ranked using a match score based on the entrant's filters.

## 3. Map View

- Entrants can view campsites on a map within a selected radius from their device location.

## 4. Advanced Review System

- Entrants can reply to reviews in threads.
- Entrants can react to reviews.

## 5. Featured Carousel

- Entrants can view a carousel of featured campsites based on their preferences.

## 6. Accessibility Mode

- Color blind mode.
- Adjustable font, button, and UI component size.

## 7. Messaging System

- Entrants and campsite owners can exchange messages about reservations.

---

## Document Information

- **Title:** Project Problem Description
- **Course:** CMPUT 301
- **Published:** June 2, 2026
- **Authors:** Abram Hindle, Michelle Deng, Raj Prasad
- **Category:** Projects
- **Tags:** projects, teams, grading
- **Source:** <https://ualberta-cmput301.github.io/projects/project_problem_descr.html>

## Archived / Commented-Out User Stories

The following user stories were present in HTML comments in the original document and are not part of the active requirements.

### Entrant

- **US 01.02.06:** As an entrant, if I win the lottery, I want to reserve a campsite for the dates I want.
- **US 01.08.02:** As an entrant, I want to add, update, or remove my profile image.
- **US 01.13.02:** As an entrant, I want to post a review for a campsite.
- **US 01.14.01:** As an entrant, I want to message the campsite owner for a reservation that I have.

### Campsite Owner

- **US 02.02.02:** As a campsite owner, I want be able to list multiple campsites with other campsite owners in a lottery for a certain period.

### Administrator

- **US 03.09.01:** As an admin, I want to remove campsite ratings that violate app policy.
- **US 03.10.01:** As an admin, I want to remove campsite reviews that violate app policy.
