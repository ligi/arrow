package arrow.validation.refinedTypes.numeric

import arrow.Kind
import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.data.Nel
import arrow.data.Validated
import arrow.data.ValidatedPartialOf
import arrow.extension
import arrow.instances.either.applicativeError.applicativeError
import arrow.instances.nonemptylist.semigroup.semigroup
import arrow.instances.validated.applicativeError.applicativeError
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Order
import arrow.validation.RefinedPredicateException
import arrow.validation.Refinement

internal fun <A : Number> isNegative(ORD: Order<A>, a: A): Boolean =
  ORD.run { a.lt(Zero.value()) }

interface Negative<F, A : Number> : Refinement<F, A> {
  fun ORD(): Order<A>

  override fun A.refinement(): Boolean = isNegative(ORD(), this)

  fun A.negative(): Kind<F, A> = refine(this)

  fun <B> A.negative(f: (A) -> B): Kind<F, B> = refine(this, f)

  override fun invalidValueMsg(a: A): String = "$a must be less than 0"
}

@extension
interface ValidatedNegative<A : Number> : Negative<ValidatedPartialOf<Nel<RefinedPredicateException>>, A> {
  override fun ORD(): Order<A>

  override fun applicativeError(): ApplicativeError<ValidatedPartialOf<Nel<RefinedPredicateException>>,
    Nel<RefinedPredicateException>> =
    Validated.applicativeError(Nel.semigroup())
}

@extension
interface EitherNegative<A : Number> : Negative<EitherPartialOf<Nel<RefinedPredicateException>>, A> {
  override fun ORD(): Order<A>

  override fun applicativeError(): ApplicativeError<EitherPartialOf<Nel<RefinedPredicateException>>,
    Nel<RefinedPredicateException>> =
    Either.applicativeError()
}