import { createRouter, createWebHistory } from 'vue-router';
import ExplorePage from '@/components/ExplorePage.vue';
import LoginPage from '@/components/LoginPage.vue';
import SignupPage from '@/components/SignupPage.vue';
import ForgetPassword from '@/components/ForgetPassword.vue';
import ServiceSelection from '../views/ServiceSelection.vue';
import ActionSelection from '../views/ActionSelection.vue';
import ActionConfiguration from '../views/ActionConfiguration.vue';
import CreateRuleView from '../views/CreateRuleView.vue';
import ChooseServiceView from '../views/ChooseServiceView.vue';
import DateTimeTriggers from '../views/DateTimeTriggers.vue';
import SetTriggerFields from '../views/SetTriggerFields.vue';
import WeatherDetails from '@/views/WeatherDetails.vue';
import SetWeatherTrigger from '@/views/SetWeatherTrigger.vue';
import SetSensorTrigger from '@/views/SetSensorTrigger.vue';
import ReviewAndFinish from '@/views/ReviewAndFinish.vue';
import SetBuildingTrigger from '@/views/SetBuildingTrigger.vue';
import AppletsView from '../views/AppletsView.vue';
import AppletsList from '../views/AppletsList.vue';

const routes = [
  {
    path: "/",
    name: "Explore",
    component: ExplorePage,
    meta: { title: "IFTTT - Explore" },
  },
  {
    path: "/login",
    name: "Login",
    component: LoginPage,
    meta: { title: "IFTTT - Log in" },
  },
  {
    path: "/signup",
    name: "SignUp",
    component: SignupPage,
    meta: { title: "IFTTT - Sign up" },
  },
  {
    path: "/forgot-password",
    name: "Forget Password",
    component: ForgetPassword,
    meta: { title: "IFTTT - Forgot Password" },
  },
  {
    path: "/services",
    name: "services",
    component: ServiceSelection,
  },
  {
    path: "/service/:serviceId/actions",
    name: "actions",
    component: ActionSelection,
  },
  {
    path: "/service/:serviceId/action/:actionId/configure",
    name: "action-configuration",
    component: ActionConfiguration,
  },
  {
    path: "/create",
    name: "create",
    component: CreateRuleView,
  },
  {
    path: "/choose-service",
    name: "choose-service",
    component: ChooseServiceView,
  },
  {
    path: "/date-time-triggers",
    name: "date-time-triggers",
    component: DateTimeTriggers,
  },
  {
    path: "/set-trigger-fields",
    name: "set-trigger-fields",
    component: SetTriggerFields,
  },
  {
    path: "/weather-details",
    name: "weather-details",
    component: WeatherDetails,
  },
  {
    path: "/review-and-finish",
    name: "review-and-finish",
    component: ReviewAndFinish,
  },
  {
    path: '/set-trigger-weather',
    name: 'set-trigger-weather',
    component: SetWeatherTrigger,
  },
  {
    path: '/set-trigger-sensor-service',
    name: 'set-trigger-sensor-service',
    component: SetSensorTrigger,
  },
  {
    path: '/set-trigger-building',
    name: 'set-trigger-building',
    component: SetBuildingTrigger,
  },
  {
    path: '/applets',
    name: 'applets',
    component: AppletsView,
    // meta: { requiresAuth: true }
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

// Set the title based on the route's meta information
router.beforeEach((to, from, next) => {
  document.title = to.meta.title || 'IFTTT';
  next();
});

export default router;
