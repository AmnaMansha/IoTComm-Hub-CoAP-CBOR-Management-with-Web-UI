<template></template>

<script>
export const actions = [
  {
    id: 'send-email',
    name: '',
    description: 'This Action will send you an HTML based email. Images and links are supported.',
    serviceId: 'email',
    fields: [
    {
        id: 'email',
        name: 'email',
        type: 'email',
        required: true
      }
    ]
  },
  {
    id: 'send-sms',
    name: 'Send an SMS',
    description: 'This Action will send an SMS from your Android device to any phone number you specify.',
    serviceId: 'android-sms',
    fields: [
      {
        id: 'phone',
        name: 'Phone number',
        type: 'text',
        required: true
      },
      {
        id: 'message',
        name: 'Message',
        type: 'text',
        required: true
      }
    ]
  },
  {
    id: 'create-calendar-event',
    name: 'Create a detailed event',
    description: 'This action will create a detailed event in your Google Calendar.',
    serviceId: 'calendar',
    fields: [
      {
        id: 'calendar',
        name: 'Google Calendar account',
        type: 'email',
        required: true
      },
      {
        id: 'which_calendar',
        name: 'Which calendar?',
        type: 'email',
        required: true
      },
      {
        id: 'title',
        name: 'Title',
        type: 'text',
        required: true
      },
      {
        id: 'description',
        name: 'Description',
        type: 'text',
        required: false
      },
      {
        id: 'location',
        name: 'Location',
        type: 'text',
        required: false
      },
      {
        id: 'attendees',
        name: 'Attendees',
        type: 'text',
        required: false,
        description: 'Comma separated list of email addresses'
      }
    ]
  },
  {
    id: 'send-basic-notification',
    name: 'Send a notification',
    description: 'This action will send a notification to your devices from the IFTTT app.',
    serviceId: 'notifications',
    fields: [
      {
        id: 'message',
        name: 'Message',
        type: 'text',
        required: true
      }
    ]
  },
  {
    id: 'send-rich-notification',
    name: 'Send a rich notification',
    description: 'This action will send a rich notification to your devices from the IFTTT app.',
    serviceId: 'notifications',
    fields: [
      {
        id: 'message',
        name: 'Message',
        type: 'text',
        required: true
      },
      {
        id: 'title',
        name: 'Title',
        type: 'text',
        required: false,
        description: 'Optional, bold text above the message'
      },
      {
        id: 'link_url',
        name: 'Link URL',
        type: 'text',
        required: false,
        description: 'Optional, link may open in a browser or installed app'
      },
      {
        id: 'image_url',
        name: 'Image URL',
        type: 'text',
        required: false,
        description: 'Optional'
      }
    ]
  },
  {
    id: 'control-led',
    name: 'Control LED',
    description: 'Turn LED on/off based on sensor readings',
    icon: '💡',
    serviceId: 'sensor-service',
    
    fields: [
    {
        id: 'email',
        name: 'email',
        type: 'email',
        required: true
      },]
  }
];
</script>