package org.oppia.app.topic

import android.content.Context
import android.content.Intent
import android.os.Bundle
import org.oppia.app.activity.InjectableAppCompatActivity
import org.oppia.app.home.RouteToExplorationListener
import org.oppia.app.player.exploration.ExplorationActivity
import org.oppia.app.story.StoryActivity
import org.oppia.app.topic.questionplayer.QuestionPlayerActivity
import org.oppia.app.topic.reviewcard.ReviewCardFragment
import org.oppia.app.topic.reviewcard.ReviewCardListener
import javax.inject.Inject

const val TOPIC_ACTIVITY_TOPIC_ID_ARGUMENT_KEY = "TopicActivity.topic_id"

/** The activity for displaying [TopicFragment]. */
class TopicActivity : InjectableAppCompatActivity(), RouteToQuestionPlayerListener,
  RouteToStoryListener, RouteToExplorationListener, RouteToReviewCardListener, ReviewCardListener {

  private lateinit var topicId: String
  private var storyId: String? = null
  @Inject lateinit var topicActivityPresenter: TopicActivityPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    activityComponent.inject(this)
    topicId = checkNotNull(intent?.getStringExtra(org.oppia.app.topic.TOPIC_ACTIVITY_TOPIC_ID_ARGUMENT_KEY)) {
      "Expected topic ID to be included in intent for TopicActivity."
    }
    storyId = intent?.getStringExtra(TOPIC_ACTIVITY_STORY_ID_ARGUMENT_KEY)
    topicActivityPresenter.handleOnCreate(topicId, storyId)
  }

  override fun routeToQuestionPlayer(skillIdList: ArrayList<String>) {
    startActivity(QuestionPlayerActivity.createQuestionPlayerActivityIntent(this, skillIdList))
  }

  override fun routeToStory(storyId: String) {
    startActivity(StoryActivity.createStoryActivityIntent(this, storyId))
  }

  override fun routeToReviewCard(topicId: String, subtopicId: String) {
    if (getReviewCardFragment() == null) {
      val reviewCardFragment: ReviewCardFragment = ReviewCardFragment.newInstance(topicId, subtopicId)
      reviewCardFragment.showNow(supportFragmentManager, TAG_REVIEW_CARD_DIALOG)
    }
  }

  override fun routeToExploration(explorationId: String, topicId: String?) {
    startActivity(ExplorationActivity.createExplorationActivityIntent(this, explorationId, topicId))
  }

  override fun dismiss() {
    getReviewCardFragment()?.dismiss()
  }

  private fun getReviewCardFragment(): ReviewCardFragment? {
    return supportFragmentManager.findFragmentByTag(TAG_REVIEW_CARD_DIALOG) as ReviewCardFragment?
  }

  companion object {
    internal const val TAG_REVIEW_CARD_DIALOG = "REVIEW_CARD_DIALOG"
    internal const val TOPIC_ACTIVITY_TOPIC_ID_ARGUMENT_KEY = "TopicActivity.topic_id"
    internal const val TOPIC_ACTIVITY_STORY_ID_ARGUMENT_KEY = "TopicActivity.story_id"

    /** Returns a new [Intent] to route to [TopicActivity] for a specified topic ID. */
    fun createTopicActivityIntent(context: Context, topicId: String): Intent {
      val intent = Intent(context, TopicActivity::class.java)
      intent.putExtra(TOPIC_ACTIVITY_TOPIC_ID_ARGUMENT_KEY, topicId)
      return intent
    }

    /** Returns a new [Intent] to route to [TopicLessonsFragment] for a specified story ID. */
    fun createTopicPlayStoryActivityIntent(context: Context, topicId: String, storyId: String): Intent {
      val intent = Intent(context, TopicActivity::class.java)
      intent.putExtra(TOPIC_ACTIVITY_TOPIC_ID_ARGUMENT_KEY, topicId)
      intent.putExtra(TOPIC_ACTIVITY_STORY_ID_ARGUMENT_KEY, storyId)
      return intent
    }
  }
}
